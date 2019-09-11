package net.corda.bridge.internal

import net.corda.bridge.FirewallVersionInfo
import net.corda.bridge.services.api.*
import net.corda.bridge.services.audit.LoggingFirewallAuditService
import net.corda.bridge.services.supervisors.BridgeSupervisorServiceImpl
import net.corda.bridge.services.supervisors.FloatSupervisorServiceImpl
import net.corda.nodeapi.internal.lifecycle.ServiceStateCombiner
import net.corda.nodeapi.internal.lifecycle.ServiceStateHelper
import net.corda.core.concurrent.CordaFuture
import net.corda.core.internal.concurrent.openFuture
import net.corda.core.internal.exists
import net.corda.core.internal.readObject
import net.corda.core.serialization.deserialize
import net.corda.core.serialization.internal.SerializationEnvironment
import net.corda.core.serialization.internal.effectiveSerializationEnv
import net.corda.core.serialization.internal.nodeSerializationEnv
import net.corda.core.utilities.contextLogger
import net.corda.nodeapi.internal.ShutdownHook
import net.corda.nodeapi.internal.addShutdownHook
import net.corda.nodeapi.internal.lifecycle.ServiceLifecycleSupport
import net.corda.nodeapi.internal.lifecycle.ServiceStateSupport
import net.corda.nodeapi.internal.network.SignedNetworkParameters
import net.corda.serialization.internal.AMQP_P2P_CONTEXT
import net.corda.serialization.internal.SerializationFactoryImpl
import rx.Subscription
import java.util.concurrent.atomic.AtomicBoolean

class FirewallInstance(val conf: FirewallConfiguration,
                       val versionInfo: FirewallVersionInfo,
                       private val stateHelper: ServiceStateHelper = ServiceStateHelper(log)) : ServiceLifecycleSupport, ServiceStateSupport by stateHelper {
    companion object {
        val log = contextLogger()
    }

    private val shutdown = AtomicBoolean(false)
    private var shutdownHook: ShutdownHook? = null

    private var maxMessageSize: Int? = null
    private var firewallAuditService: FirewallAuditService? = null
    private var bridgeSupervisorService: BridgeSupervisorService? = null
    private var floatSupervisorService: FloatSupervisorService? = null
    private var statusFollower: ServiceStateCombiner? = null
    private var statusSubscriber: Subscription? = null


    init {
        initialiseSerialization()
    }

    private fun initialiseSerialization() {
        val serializationExists = try {
            effectiveSerializationEnv
            true
        } catch (e: IllegalStateException) {
            false
        }
        if (!serializationExists) {
            val classloader = this.javaClass.classLoader
            nodeSerializationEnv = SerializationEnvironment.with(
                    SerializationFactoryImpl().apply {
                        registerScheme(AMQPFirewallSerializationScheme(emptyList()))
                    },
                    p2pContext = AMQP_P2P_CONTEXT.withClassLoader(classloader))
        }
    }

    override fun start() {
        val wasRunning = shutdown.getAndSet(true)
        require(!wasRunning) { "Already running" }
        shutdownHook = addShutdownHook {
            stop()
        }
        if (conf.firewallMode != FirewallMode.FloatOuter) {
            retrieveNetworkParameters()
        }
        createServices()
        startServices()
    }

    override fun stop() {
        val wasRunning = shutdown.getAndSet(false)
        if (!wasRunning) {
            return
        }
        shutdownHook?.cancel()
        shutdownHook = null
        log.info("Shutting down ...")

        stopServices()

        _exitFuture.set(this)
        log.info("Shutdown complete")
    }

    private val _exitFuture = openFuture<FirewallInstance>()
    val onExit: CordaFuture<FirewallInstance> get() = _exitFuture

    private fun retrieveNetworkParameters() {
        val networkParamsFile = requireNotNull(conf.networkParametersPath) { "networkParametersPath must be specified." }

        require(networkParamsFile.exists()) { "No network-parameters file found." }
        val networkParameters = networkParamsFile.readObject<SignedNetworkParameters>().raw.deserialize()
        maxMessageSize = networkParameters.maxMessageSize
        log.info("Loaded maxMessageSize from network-parameters file: $maxMessageSize")
    }

    private fun createServices() {
        firewallAuditService = LoggingFirewallAuditService(conf)
        when (conf.firewallMode) {
        // In the SenderReceiver mode the inbound and outbound message paths are run from within a single firewall process.
        // The process thus contains components that listen for bridge control messages on Artemis.
        // The process can then initiates TLS/AMQP 1.0 connections to remote peers and transfers the outbound messages.
        // The process also runs a TLS/AMQP 1.0 server socket, which is can receive connections and messages from peers,
        // validate the messages and then forwards the packets to the Artemis inbox queue of the node.
            FirewallMode.SenderReceiver -> {
                require(maxMessageSize != null && maxMessageSize!! > 0) { "maxMessageSize not initialised" }
                floatSupervisorService = FloatSupervisorServiceImpl(conf, firewallAuditService!!)
                bridgeSupervisorService = BridgeSupervisorServiceImpl(conf, maxMessageSize!!, firewallAuditService!!, floatSupervisorService!!.amqpListenerService)
            }
        // In the BridgeInner mode the process runs the full outbound message path as in the SenderReceiver mode, but the inbound path is split.
        // This 'Bridge Inner/Bridge Controller' process runs the more trusted portion of the inbound path.
        // In particular the 'Bridge Inner/Bridge Controller' has access to the persisted TLS KeyStore, which it provisions dynamically into the 'Float Outer'.
        // Also the the 'Bridge Inner' does more complete validation of inbound messages and ensures that they correspond to legitimate
        // node inboxes, before transferring the message to Artemis. Potentially it might carry out deeper checks of received packets.
        // However, the 'Bridge Inner' is not directly exposed to the internet, or peers and does not host the TLS/AMQP 1.0 server socket.
            FirewallMode.BridgeInner -> {
                require(maxMessageSize != null && maxMessageSize!! > 0) { "maxMessageSize not initialised" }
                bridgeSupervisorService = BridgeSupervisorServiceImpl(conf, maxMessageSize!!, firewallAuditService!!, null)
            }
        // In the FloatOuter mode this process runs a minimal AMQP proxy that is designed to run in a DMZ zone.
        // The process holds the minimum data necessary to act as the TLS/AMQP 1.0 receiver socket and tries
        // to minimise any state. It specifically does not persist the Node TLS keys anywhere, nor does it hold network map information on peers.
        // The 'Float Outer' does not initiate socket connection anywhere, so that attackers can be easily blocked by firewalls
        // if they try to invade the system from a compromised 'Float Outer' machine. The 'Float Outer' hosts a control TLS/AMQP 1.0 server socket,
        // which receives a connection from the 'Bridge Inner/Bridge controller' in the trusted zone of the organisation.
        // The control channel is ideally authenticated using server/client certificates that are not related to the Corda PKI hierarchy.
        // Once the control channel is formed it is used to RPC the methods of the BridgeAMQPListenerService to start the publicly visible
        // TLS/AMQP 1.0 server socket of the Corda node. Thus peer connections will directly terminate onto the activate listener socket and
        // be validated against the keys/certificates sent across the control tunnel. Inbound messages are given basic checks that do not require
        // holding potentially sensitive information and are then forwarded across the control tunnel to the 'Bridge Inner' process for more
        // complete validation checks.
            FirewallMode.FloatOuter -> {
                floatSupervisorService = FloatSupervisorServiceImpl(conf, firewallAuditService!!)
            }
        }
        statusFollower = ServiceStateCombiner(listOf(firewallAuditService, floatSupervisorService, bridgeSupervisorService).filterNotNull())
        statusSubscriber = statusFollower!!.activeChange.subscribe({
            stateHelper.active = it
        }, { log.error("Error in state change", it) })
    }

    private fun startServices() {
        firewallAuditService?.start()
        bridgeSupervisorService?.start()
        floatSupervisorService?.start()
    }

    private fun stopServices() {
        stateHelper.active = false
        floatSupervisorService?.stop()
        bridgeSupervisorService?.stop()
        firewallAuditService?.stop()
        statusSubscriber?.unsubscribe()
        statusSubscriber = null
        statusFollower = null
    }
}