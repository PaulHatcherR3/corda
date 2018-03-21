package com.r3.corda.networkmanage

import com.r3.corda.networkmanage.common.persistence.entity.NetworkMapEntity
import com.r3.corda.networkmanage.common.persistence.entity.NetworkParametersEntity
import net.corda.core.crypto.SecureHash
import net.corda.core.node.NetworkParameters
import net.corda.core.serialization.serialize
import net.corda.nodeapi.internal.createDevNetworkMapCa
import net.corda.nodeapi.internal.crypto.CertificateAndKeyPair
import net.corda.nodeapi.internal.network.NetworkMap
import net.corda.nodeapi.internal.network.ParametersUpdate
import net.corda.testing.common.internal.testNetworkParameters

fun createNetworkParametersEntity(signingCertAndKeyPair: CertificateAndKeyPair = createDevNetworkMapCa(),
                                  networkParameters: NetworkParameters = testNetworkParameters()): NetworkParametersEntity {
    val signedNetParams = signingCertAndKeyPair.sign(networkParameters)
    return NetworkParametersEntity(
            parametersHash = signedNetParams.raw.hash.toString(),
            parametersBytes = signedNetParams.raw.bytes,
            signature = signedNetParams.sig.bytes,
            certificate = signedNetParams.sig.by.encoded
    )
}

fun createNetworkParametersEntityUnsigned(networkParameters: NetworkParameters = testNetworkParameters()): NetworkParametersEntity {
    val serialised = networkParameters.serialize()
    return NetworkParametersEntity(
            parametersHash = serialised.hash.toString(),
            parametersBytes = serialised.bytes,
            signature = null,
            certificate = null
    )
}

fun createNetworkMapEntity(signingCertAndKeyPair: CertificateAndKeyPair = createDevNetworkMapCa(),
                           netParamsEntity: NetworkParametersEntity,
                           nodeInfoHashes: List<SecureHash> = emptyList(),
                           parametersUpdate: ParametersUpdate? = null): NetworkMapEntity {
    val signedNetMap = signingCertAndKeyPair.sign(NetworkMap(nodeInfoHashes, SecureHash.parse(netParamsEntity.parametersHash), parametersUpdate))
    return NetworkMapEntity(
            networkMapBytes = signedNetMap.raw.bytes,
            signature = signedNetMap.sig.bytes,
            certificate = signedNetMap.sig.by.encoded,
            networkParameters = netParamsEntity
    )
}

fun createNetworkMapEntity(signingCertAndKeyPair: CertificateAndKeyPair = createDevNetworkMapCa(),
                           networkParameters: NetworkParameters = testNetworkParameters(),
                           nodeInfoHashes: List<SecureHash> = emptyList()): NetworkMapEntity {
    val netParamsEntity = createNetworkParametersEntity(signingCertAndKeyPair, networkParameters)
    return createNetworkMapEntity(signingCertAndKeyPair, netParamsEntity, nodeInfoHashes)
}
