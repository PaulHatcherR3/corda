package com.r3.ha.utilities

import net.corda.cliutils.CommonCliConstants.BASE_DIR
import net.corda.core.crypto.Crypto
import net.corda.core.identity.CordaX500Name
import net.corda.core.internal.div
import net.corda.core.utilities.contextLogger
import net.corda.nodeapi.internal.config.CertificateStore
import net.corda.nodeapi.internal.config.FileBasedCertificateStoreSupplier
import net.corda.nodeapi.internal.crypto.CertificateAndKeyPair
import net.corda.nodeapi.internal.crypto.CertificateType
import net.corda.nodeapi.internal.crypto.X509KeyStore
import net.corda.nodeapi.internal.crypto.X509Utilities
import net.corda.nodeapi.internal.cryptoservice.CryptoService
import net.corda.nodeapi.internal.cryptoservice.CryptoServiceFactory
import net.corda.nodeapi.internal.cryptoservice.SupportedCryptoServices
import net.corda.nodeapi.internal.cryptoservice.bouncycastle.BCCryptoService
import picocli.CommandLine.Option
import sun.security.x509.X500Name
import java.nio.file.Path
import java.nio.file.Paths
import javax.security.auth.x500.X500Principal

private const val DEFAULT_PASSWORD = "changeit"
private const val HSM_LIST = "Azure, Utimaco, Gemalto, Futurex, Securosys"
private val hsmOptionMap = mapOf(
        'a' to SupportedCryptoServices.AZURE_KEY_VAULT,
        'u' to SupportedCryptoServices.UTIMACO,
        'g' to SupportedCryptoServices.GEMALTO_LUNA,
        'f' to SupportedCryptoServices.FUTUREX,
        's' to SupportedCryptoServices.PRIMUS_X)

class
InternalArtemisKeystoreGenerator : AbstractInternalKeystoreGenerator("generate-internal-artemis-ssl-keystores", "Generate self-signed root and SSL certificates for internal communication between the services and external Artemis broker.") {
    companion object {
        private val logger by lazy { contextLogger() }
    }

    @Option(names = ["--bridge-hsm-name", "-m"],
            description = ["The Bridge HSM name. One of ${HSM_LIST}. The first x characters to uniquely identify the name is adequate (case insensitive)"])
    var cryptoServiceNameBridge: String? = null
    @Option(names = ["--bridge-hsm-config-file", "-f"], paramLabel = "FILE", description = ["The path to the HSM config file. Only required if the HSM name has been specified"])
    var cryptoServiceConfigFileBridge: Path? = null

    @Option(names = ["--node-hsm-name", "-s"],
            description = ["The Node HSM name. One of ${HSM_LIST}. The first x characters to uniquely identify the name is adequate (case insensitive)"])
    var cryptoServiceNameNode: String? = null
    @Option(names = ["--node-hsm-config-file", "-i"], paramLabel = "FILE", description = ["The path to the HSM config file. Only required if the HSM name has been specified"])
    var cryptoServiceConfigFileNode: Path? = null


    override fun createKeyStores() {
        logger.info("Generating Artemis keystores")
        if (errorInHSMOptions(cryptoServiceNameBridge, cryptoServiceConfigFileBridge, "Error in Bridge HSM options for Artemis key")) {
            throw IllegalArgumentException("Error in HSM options for Artemis key")
        }
        if (errorInHSMOptions(cryptoServiceNameNode, cryptoServiceConfigFileNode, "Error in Node HSM options for Artemis key")) {
            throw IllegalArgumentException("Error in HSM options for Artemis key")
        }
        val artemisCertDir = baseDirectory / "artemis"
        val root = createRootKeystore("Internal Artemis Root", artemisCertDir / "artemis-root.jks", artemisCertDir / "artemis-truststore.jks",
                keyStorePassword, keyStorePassword, trustStorePassword).getCertificateAndKeyPair(X509Utilities.CORDA_ROOT_CA, keyStorePassword)

        createArtemisKeystore( "NodeArtemis","artemisnode.jks", root, cryptoServiceNameNode, cryptoServiceConfigFileNode)
        createArtemisKeystore( "BridgeArtemis", "artemisbridge.jks", root, cryptoServiceNameBridge, cryptoServiceConfigFileBridge)
        createFileBasedTLSKeystore("artemis", root, artemisCertDir / "artemis.jks",
                keyStorePassword, keyStorePassword, X509Utilities.CORDA_CLIENT_TLS)
    }

    private fun createArtemisKeystore(commonName: String, keystoreFileName: String, root: CertificateAndKeyPair,
                                      cryptoServiceName: String?, cryptoServiceConfigFile: Path?) {
        val artemisCertDir = baseDirectory / "artemis"

        val x500Name = CordaX500Name( commonName, organizationUnit, organization, locality, null, country )
        val cryptoService = createCryptoService(cryptoServiceName, cryptoServiceConfigFile,
                artemisCertDir / keystoreFileName, keyStorePassword, keyStorePassword, x500Name)

        createTLSKeystore("artemis", root, artemisCertDir / keystoreFileName, keyStorePassword, keyStorePassword,
                "${X509Utilities.CORDA_CLIENT_TLS}$commonName", cryptoService)
    }
}

class InternalTunnelKeystoreGenerator : AbstractInternalKeystoreGenerator("generate-internal-tunnel-ssl-keystores", "Generate self-signed root and SSL certificates for internal communication between Bridge and Float.") {
    companion object {
        private val logger by lazy { contextLogger() }
    }

        @Option(names = ["--float-hsm-name", "-m"],
                description = ["The HSM name for the Float. One of ${HSM_LIST}. The first x characters to uniquely identify the name is adequate (case insensitive)"])
        var cryptoServiceNameFloat: String? = null
        @Option(names = ["--float-hsm-config-file", "-f"], paramLabel = "FILE",
                description = ["The path to the Float HSM config file"])
        var cryptoServiceConfigFileFloat: Path? = null

        @Option(names = ["--bridge-hsm-name", "-s"],
                description = ["The HSM name for the Bridge. One of ${HSM_LIST}. The first x characters to uniquely identify the name is adequate (case insensitive)"])
        var cryptoServiceNameBridge: String? = null
        @Option(names = ["--bridge-hsm-config-file", "-i"], paramLabel = "FILE",
                description = ["The path to the Bridge HSM config file"])
        var cryptoServiceConfigFileBridge: Path? = null


    @Option(names = ["-e", "--entryPassword"], description = ["Password for all the keystores private keys."], defaultValue = DEFAULT_PASSWORD)
    lateinit var entryPassword: String

    override fun createKeyStores() {
        logger.info("Generating Tunnel keystores")

        if (errorInHSMOptions(cryptoServiceNameFloat, cryptoServiceConfigFileFloat, "Error in HSM options for Float")) {
            throw IllegalArgumentException("Error in HSM options for Float")
        }
        if (errorInHSMOptions(cryptoServiceNameBridge, cryptoServiceConfigFileBridge, "Error in HSM options for Bridge")) {
            throw IllegalArgumentException("Error in HSM options for Bridge")
        }
        val tunnelCertDir = baseDirectory / "tunnel"
        val tunnelRoot = createRootKeystore("Internal Tunnel Root", tunnelCertDir / "tunnel-root.jks", tunnelCertDir / "tunnel-truststore.jks",
                keyStorePassword, entryPassword, trustStorePassword).getCertificateAndKeyPair(X509Utilities.CORDA_ROOT_CA, entryPassword)

        val floatX500Name = CordaX500Name("Float", organizationUnit, organization, locality, null, country)
        val floatCryptoService = createCryptoService(cryptoServiceNameFloat, cryptoServiceConfigFileFloat,
                tunnelCertDir / "float.jks", keyStorePassword, entryPassword, floatX500Name)

        val bridgeX500Name = CordaX500Name("Bridge", organizationUnit, organization, locality, null, country)
        val bridgeCryptoService = createCryptoService(cryptoServiceNameBridge, cryptoServiceConfigFileBridge,
                tunnelCertDir / "bridge.jks", keyStorePassword, entryPassword, bridgeX500Name)

        createTLSKeystore("float", tunnelRoot, tunnelCertDir / "float.jks", keyStorePassword, entryPassword,
                           "${X509Utilities.CORDA_CLIENT_TLS}float", floatCryptoService)
        createTLSKeystore("bridge", tunnelRoot, tunnelCertDir / "bridge.jks", keyStorePassword, entryPassword,
                           "${X509Utilities.CORDA_CLIENT_TLS}bridge", bridgeCryptoService)

        warnOnDefaultPassword(entryPassword, "entryPassword")
    }
}

abstract class AbstractInternalKeystoreGenerator(alias: String, description: String) : HAToolBase(alias, description) {

    @Option(names = ["-b", BASE_DIR], description = ["The node working directory where all the files are kept."])
    var baseDirectory: Path = Paths.get(".").toAbsolutePath().normalize()
    @Option(names = ["-p", "--keyStorePassword"], description = ["Password for all generated keystores."], defaultValue = DEFAULT_PASSWORD)
    lateinit var keyStorePassword: String
    @Option(names = ["-t", "--trustStorePassword"], description = ["Password for the trust store."], defaultValue = DEFAULT_PASSWORD)
    lateinit var trustStorePassword: String
    @Option(names = ["-o", "--organization"], description = ["X500Name's organization attribute."], defaultValue = "Corda")
    lateinit var organization: String
    @Option(names = ["-u", "--organization-unit"], description = ["X500Name's organization unit attribute."], required = false)
    var organizationUnit: String? = null
    @Option(names = ["-l", "--locality"], description = ["X500Name's locality attribute."], defaultValue = "London")
    lateinit var locality: String
    @Option(names = ["-c", "--country"], description = ["X500Name's country attribute."], defaultValue = "GB")
    lateinit var country: String

    protected abstract fun createKeyStores()

    companion object {
        private val logger by lazy { contextLogger() }
    }

    override val driversParentDir: Path? get() = baseDirectory

    override fun runTool() {
        createKeyStores()

        listOf(keyStorePassword to "keyStorePassword", trustStorePassword to "trustStorePassword").forEach {
            warnOnDefaultPassword(it.first, it.second)
        }
    }

    protected fun warnOnDefaultPassword(password: String, paramName: String) {
        if (password == DEFAULT_PASSWORD) {
            logger.warn("Password for '$paramName' is defaulted to '$DEFAULT_PASSWORD'. Please consider changing the password using java keytool.")
        }
    }

    protected fun createRootKeystore(commonName: String, keystorePath: Path, trustStorePath: Path, storePassword: String, entryPassword: String, trustStorePassword: String): X509KeyStore {
        val key = Crypto.generateKeyPair(X509Utilities.DEFAULT_TLS_SIGNATURE_SCHEME)
        val rootCert = X509Utilities.createSelfSignedCACertificate(getX500Principal(commonName), key)
        val keystore = X509KeyStore.fromFile(keystorePath, storePassword, createNew = true)
        keystore.update {
            setPrivateKey(X509Utilities.CORDA_ROOT_CA, key.private, listOf(rootCert), entryPassword)
        }
        logger.info("$commonName keystore created in $keystorePath.")

        X509KeyStore.fromFile(trustStorePath, trustStorePassword, createNew = true).setCertificate(X509Utilities.CORDA_ROOT_CA, rootCert)
        logger.info("$commonName truststore created in $trustStorePath.")

        return keystore
    }

    protected fun createFileBasedTLSKeystore(serviceName: String, root: CertificateAndKeyPair, keystorePath: Path,
                                    storePassword: String, entryPassword: String, alias: String) {

        val certificateStoreSupplier = FileBasedCertificateStoreSupplier(keystorePath, storePassword, entryPassword)
        val cryptoService = BCCryptoService(CordaX500Name("dummy identity", "London", "GB").x500Principal,
                requireNotNull(certificateStoreSupplier) { "Fallback keystore must not be null when Crypto service config is not provided." })
        createTLSKeystore(serviceName, root, keystorePath, storePassword, entryPassword, alias, cryptoService)
    }

    protected fun createTLSKeystore(serviceName: String, root: CertificateAndKeyPair,
                                    keystorePath: Path, storePassword: String, entryPassword: String, alias: String,
                                    cryptoService: CryptoService) {

        val publicKey = cryptoService.generateKeyPair(alias, X509Utilities.DEFAULT_TLS_SIGNATURE_SCHEME)
        val cert = X509Utilities.createCertificate(CertificateType.TLS, root.certificate, root.keyPair, getX500Principal(serviceName), publicKey)

        val keyStore = CertificateStore.fromFile(keystorePath, storePassword, entryPassword, createNew = true)
        keyStore.setCertPathOnly(alias, listOf(cert, root.certificate))
        keyStore.value.save()
        logger.info("Internal TLS keystore for '$serviceName' created in $keystorePath.")
    }

    private fun getX500Principal(commonName: String): X500Principal {
        return if (organizationUnit == null) {
            "CN=$commonName, O=$organization, L=$locality, C=$country"
        } else {
            "CN=$commonName, OU=$organizationUnit, O=$organization, L=$locality, C=$country"
        }.let { X500Name(it).asX500Principal() }
    }

    fun createCryptoService(cryptoServiceName: String?, cryptoServiceConfigPath: Path?,
                            keystorePath: Path, storePassword: String, entryPassword: String, x500Name: CordaX500Name): CryptoService {

        val supportedCryptoServiceName = resolveCryptoServiceName(cryptoServiceName)
        logger.logCryptoServiceName(supportedCryptoServiceName, x500Name)
        val certificateStoreSupplier = when (supportedCryptoServiceName) {
            SupportedCryptoServices.BC_SIMPLE -> FileBasedCertificateStoreSupplier(keystorePath, storePassword, entryPassword)
            else -> null
        }
        logger.logConfigPath(cryptoServiceConfigPath)
        return CryptoServiceFactory.makeCryptoService(supportedCryptoServiceName, x500Name, certificateStoreSupplier, cryptoServiceConfigPath)
    }

    fun resolveCryptoServiceName(cryptoServiceName: String?): SupportedCryptoServices {
        if (cryptoServiceName.isNullOrEmpty()) {
            return SupportedCryptoServices.BC_SIMPLE
        }
        return hsmOptionMap.getOrDefault(cryptoServiceName!!.toLowerCase()[0], SupportedCryptoServices.BC_SIMPLE)
    }

    // When we move to Picocli 4.0 delete this and use @ArgGroup instead.
    fun errorInHSMOptions(hsmName: String?, hsmConfigFile: Path?, prefix: String): Boolean {
        if (hsmName.isNullOrBlank() && hsmConfigFile != null) {
            logger.error(prefix)
            logger.error("If the HSM config file option is specified the HSM name option must also be specified")
            return true
        }
        if (!hsmName.isNullOrBlank() && hsmConfigFile == null) {
            logger.error(prefix)
            logger.error("If the HSM name option is specified the HSM config file option must also be specified")
            return true
        }
        return false
    }
}