package net.corda.client.jackson

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.BinaryNode
import com.fasterxml.jackson.databind.node.IntNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.convertValue
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.nhaarman.mockito_kotlin.spy
import net.corda.client.jackson.internal.childrenAs
import net.corda.client.jackson.internal.valueAs
import net.corda.core.contracts.*
import net.corda.core.cordapp.CordappProvider
import net.corda.core.crypto.*
import net.corda.core.crypto.CompositeKey
import net.corda.core.crypto.PartialMerkleTree.PartialTree
import net.corda.core.identity.*
import net.corda.core.internal.DigitalSignatureWithCert
import net.corda.core.node.NodeInfo
import net.corda.core.node.ServiceHub
import net.corda.core.node.services.NetworkParametersService
import net.corda.core.serialization.CordaSerializable
import net.corda.core.serialization.SerializedBytes
import net.corda.core.serialization.deserialize
import net.corda.core.serialization.serialize
import net.corda.core.utilities.*
import net.corda.finance.USD
import net.corda.nodeapi.internal.crypto.x509Certificates
import net.corda.testing.common.internal.testNetworkParameters
import net.corda.testing.core.*
import net.corda.testing.internal.createNodeInfoAndSigned
import net.corda.testing.internal.rigorousMock
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import java.math.BigInteger
import java.nio.charset.StandardCharsets.UTF_8
import java.security.PublicKey
import java.security.cert.CertPath
import java.security.cert.X509Certificate
import java.time.Instant
import java.util.*
import javax.security.auth.x500.X500Principal
import kotlin.collections.ArrayList

@RunWith(Parameterized::class)
class JacksonSupportTest(@Suppress("unused") private val name: String, factory: JsonFactory) {
    private companion object {
        val SEED: BigInteger = BigInteger.valueOf(20170922L)
        val ALICE_PUBKEY = TestIdentity(ALICE_NAME, 70).publicKey
        val BOB_PUBKEY = TestIdentity(BOB_NAME, 80).publicKey
        val DUMMY_NOTARY = TestIdentity(DUMMY_NOTARY_NAME, 20).party
        val MINI_CORP = TestIdentity(CordaX500Name("MiniCorp", "London", "GB"))

        @Parameters(name = "{0}")
        @JvmStatic
        fun factories() = arrayOf(arrayOf("JSON", JsonFactory()), arrayOf("YAML", YAMLFactory()))
    }

    @Rule
    @JvmField
    val testSerialization = SerializationEnvironmentRule()

    private val partyObjectMapper = TestPartyObjectMapper()
    private val mapper = JacksonSupport.createPartyObjectMapper(partyObjectMapper, factory)

    private lateinit var services: ServiceHub
    private lateinit var cordappProvider: CordappProvider

    @Before
    fun setup() {
        services = rigorousMock()
        cordappProvider = rigorousMock()
        val networkParameters = testNetworkParameters(minimumPlatformVersion = 4)
        val networkParametersService = rigorousMock<NetworkParametersService>().also {
            doReturn(networkParameters.serialize().hash).whenever(it).currentHash
        }
        doReturn(networkParametersService).whenever(services).networkParametersService
        doReturn(cordappProvider).whenever(services).cordappProvider
        doReturn(networkParameters).whenever(services).networkParameters
    }

    @Test(timeout=300_000)
	fun `Amount(Currency) serialization`() {
        assertThat(mapper.valueToTree<TextNode>(Amount.parseCurrency("£25000000")).textValue()).isEqualTo("25000000.00 GBP")
        assertThat(mapper.valueToTree<TextNode>(Amount.parseCurrency("$250000")).textValue()).isEqualTo("250000.00 USD")
    }

    @Test(timeout=300_000)
	fun `Amount(Currency) deserialization`() {
        val old = mapOf(
            "quantity" to 2500000000,
            "token" to "USD"
        )
        assertThat(mapper.convertValue<Amount<Currency>>(old)).isEqualTo(Amount(2_500_000_000, USD))
    }

    @Test(timeout=300_000)
	fun `Amount(Currency) Text deserialization`() {
        assertThat(mapper.convertValue<Amount<Currency>>(TextNode("$25000000"))).isEqualTo(Amount(2_500_000_000, USD))
    }

    @Test(timeout=300_000)
	fun ByteSequence() {
        val byteSequence: ByteSequence = OpaqueBytes.of(1, 2, 3, 4).subSequence(0, 2)
        val json = mapper.valueToTree<BinaryNode>(byteSequence)
        assertThat(json.binaryValue()).containsExactly(1, 2)
        assertThat(json.asText()).isEqualTo(byteArrayOf(1, 2).toBase64())
        assertThat(mapper.convertValue<ByteSequence>(json)).isEqualTo(byteSequence)
    }

    @Test(timeout=300_000)
	fun `OpaqueBytes serialization`() {
        val opaqueBytes = OpaqueBytes(secureRandomBytes(128))
        val json = mapper.valueToTree<BinaryNode>(opaqueBytes)
        assertThat(json.binaryValue()).isEqualTo(opaqueBytes.bytes)
        assertThat(json.asText()).isEqualTo(opaqueBytes.bytes.toBase64())
    }

    @Test(timeout=300_000)
	fun `OpaqueBytes deserialization`() {
        assertThat(mapper.convertValue<OpaqueBytes>(TextNode("1234"))).isEqualTo(OpaqueBytes("1234".toByteArray(UTF_8)))
        assertThat(mapper.convertValue<OpaqueBytes>(BinaryNode(byteArrayOf(1, 2, 3, 4)))).isEqualTo(OpaqueBytes.of(1, 2, 3, 4))
    }

    @Test(timeout=300_000)
	fun SerializedBytes() {
        val data = TestData(BOB_NAME, "Summary", SubTestData(1234))
        val serializedBytes = data.serialize()
        val json = mapper.valueToTree<ObjectNode>(serializedBytes)
        println(mapper.writeValueAsString(json))
        assertThat(json["class"].textValue()).isEqualTo(TestData::class.java.name)
        assertThat(json["deserialized"].valueAs<TestData>(mapper)).isEqualTo(data)
        // Check that the entire JSON object can be converted back to the same SerializedBytes
        assertThat(mapper.convertValue<SerializedBytes<*>>(json)).isEqualTo(serializedBytes)
        assertThat(mapper.convertValue<SerializedBytes<*>>(BinaryNode(serializedBytes.bytes))).isEqualTo(serializedBytes)
    }

    @Test(timeout=300_000)
	fun `SerializedBytes of class not on classpath`() {
        // The contents of the file were written out as follows:
//        ClassNotOnClasspath(BOB_NAME, 54321).serialize().open().copyTo("build" / "class-not-on-classpath-data")

        val serializedBytes = SerializedBytes<Any>(javaClass.getResource("class-not-on-classpath-data").readBytes())
        val json = mapper.valueToTree<ObjectNode>(serializedBytes)
        println(mapper.writeValueAsString(json))
        assertThat(json["class"].textValue()).isEqualTo("net.corda.client.jackson.JacksonSupportTest\$ClassNotOnClasspath")
        assertThat(json["deserialized"].valueAs<Map<*, *>>(mapper)).isEqualTo(mapOf(
                "name" to BOB_NAME.toString(),
                "value" to 54321
        ))
        assertThat(mapper.convertValue<SerializedBytes<*>>(BinaryNode(serializedBytes.bytes))).isEqualTo(serializedBytes)
    }

    @Test(timeout=300_000)
	fun DigitalSignature() {
        val digitalSignature = DigitalSignature(secureRandomBytes(128))
        val json = mapper.valueToTree<BinaryNode>(digitalSignature)
        assertThat(json.binaryValue()).isEqualTo(digitalSignature.bytes)
        assertThat(json.asText()).isEqualTo(digitalSignature.bytes.toBase64())
        assertThat(mapper.convertValue<DigitalSignature>(json)).isEqualTo(digitalSignature)
    }

    @Test(timeout=300_000)
	fun `DigitalSignature WithKey`() {
        val digitalSignature = DigitalSignature.WithKey(BOB_PUBKEY, secureRandomBytes(128))
        val json = mapper.valueToTree<ObjectNode>(digitalSignature)
        val (by, bytes) = json.assertHasOnlyFields("by", "bytes")
        assertThat(by.valueAs<PublicKey>(mapper)).isEqualTo(BOB_PUBKEY)
        assertThat(bytes.binaryValue()).isEqualTo(digitalSignature.bytes)
        assertThat(mapper.convertValue<DigitalSignature.WithKey>(json)).isEqualTo(digitalSignature)
    }

    @Test(timeout=300_000)
	fun DigitalSignatureWithCert() {
        val digitalSignature = DigitalSignatureWithCert(MINI_CORP.identity.certificate, secureRandomBytes(128))
        val json = mapper.valueToTree<ObjectNode>(digitalSignature)
        val (by, bytes) = json.assertHasOnlyFields("by", "bytes", "parentCertsChain")
        assertThat(by.valueAs<X509Certificate>(mapper)).isEqualTo(MINI_CORP.identity.certificate)
        assertThat(bytes.binaryValue()).isEqualTo(digitalSignature.bytes)
        assertThat(mapper.convertValue<DigitalSignatureWithCert>(json)).isEqualTo(digitalSignature)
    }

    @Test(timeout=300_000)
	fun TransactionSignature() {
        val signatureMetadata = SignatureMetadata(1, 1)
        val partialMerkleTree = PartialMerkleTree(PartialTree.Node(
                left = PartialTree.Leaf(SecureHash.randomSHA256()),
                right = PartialTree.IncludedLeaf(SecureHash.randomSHA256())
        ))
        val transactionSignature = TransactionSignature(secureRandomBytes(128), BOB_PUBKEY, signatureMetadata, partialMerkleTree)
        val json = mapper.valueToTree<ObjectNode>(transactionSignature)
        val (bytesJson, byJson, signatureMetadataJson, partialMerkleTreeJson) = json.assertHasOnlyFields(
                "bytes",
                "by",
                "signatureMetadata",
                "partialMerkleTree"
        )
        assertThat(bytesJson.binaryValue()).isEqualTo(transactionSignature.bytes)
        assertThat(byJson.valueAs<PublicKey>(mapper)).isEqualTo(BOB_PUBKEY)
        assertThat(signatureMetadataJson.valueAs<SignatureMetadata>(mapper)).isEqualTo(signatureMetadata)
        assertThat(partialMerkleTreeJson.valueAs<PartialMerkleTree>(mapper).root).isEqualTo(partialMerkleTree.root)
        assertThat(mapper.convertValue<TransactionSignature>(json)).isEqualTo(transactionSignature)
    }

    @Test(timeout=300_000)
	fun PrivacySalt() {
        val privacySalt = net.corda.core.contracts.PrivacySalt()
        val json = mapper.valueToTree<TextNode>(privacySalt)
        assertThat(json.textValue()).isEqualTo(privacySalt.bytes.toHexString())
        assertThat(mapper.convertValue<PrivacySalt>(json)).isEqualTo(privacySalt)
    }

    @Test(timeout=300_000)
	fun SignatureMetadata() {
        val signatureMetadata = SignatureMetadata(2, Crypto.ECDSA_SECP256R1_SHA256.schemeNumberID)
        val json = mapper.valueToTree<ObjectNode>(signatureMetadata)
        val (platformVersion, scheme) = json.assertHasOnlyFields("platformVersion", "scheme")
        assertThat(platformVersion.intValue()).isEqualTo(2)
        assertThat(scheme.textValue()).isEqualTo("ECDSA_SECP256R1_SHA256")
        assertThat(mapper.convertValue<SignatureMetadata>(json)).isEqualTo(signatureMetadata)
    }

    @Test(timeout=300_000)
	fun `SignatureMetadata on unknown schemeNumberID`() {
        val signatureMetadata = SignatureMetadata(2, Int.MAX_VALUE)
        val json = mapper.valueToTree<ObjectNode>(signatureMetadata)
        assertThat(json["scheme"].intValue()).isEqualTo(Int.MAX_VALUE)
        assertThat(mapper.convertValue<SignatureMetadata>(json)).isEqualTo(signatureMetadata)
    }

    @Test(timeout=300_000)
	fun `SignatureScheme serialization`() {
        val json = mapper.valueToTree<TextNode>(Crypto.ECDSA_SECP256R1_SHA256)
        assertThat(json.textValue()).isEqualTo("ECDSA_SECP256R1_SHA256")
    }

    @Test(timeout=300_000)
	fun `SignatureScheme deserialization`() {
        assertThat(mapper.convertValue<SignatureScheme>(TextNode("EDDSA_ED25519_SHA512"))).isSameAs(Crypto.EDDSA_ED25519_SHA512)
        assertThat(mapper.convertValue<SignatureScheme>(IntNode(4))).isSameAs(Crypto.EDDSA_ED25519_SHA512)
    }

    @Test(timeout=300_000)
	fun `PartialTree IncludedLeaf`() {
        val includedLeaf = PartialTree.IncludedLeaf(SecureHash.randomSHA256())
        val json = mapper.valueToTree<ObjectNode>(includedLeaf)
        assertThat(json.assertHasOnlyFields("includedLeaf")[0].textValue()).isEqualTo(includedLeaf.hash.toString())
        assertThat(mapper.convertValue<PartialTree.IncludedLeaf>(json)).isEqualTo(includedLeaf)
    }

    @Test(timeout=300_000)
	fun `PartialTree Leaf`() {
        val leaf = PartialTree.Leaf(SecureHash.randomSHA256())
        val json = mapper.valueToTree<ObjectNode>(leaf)
        assertThat(json.assertHasOnlyFields("leaf")[0].textValue()).isEqualTo(leaf.hash.toString())
        assertThat(mapper.convertValue<PartialTree.Leaf>(json)).isEqualTo(leaf)
    }

    @Test(timeout=300_000)
	fun `simple PartialTree Node`() {
        val node = PartialTree.Node(
                left = PartialTree.Leaf(SecureHash.randomSHA256()),
                right = PartialTree.IncludedLeaf(SecureHash.randomSHA256())
        )
        val json = mapper.valueToTree<ObjectNode>(node)
        println(mapper.writeValueAsString(json))
        val (leftJson, rightJson) = json.assertHasOnlyFields("left", "right")
        assertThat(leftJson.valueAs<PartialTree>(mapper)).isEqualTo(node.left)
        assertThat(rightJson.valueAs<PartialTree>(mapper)).isEqualTo(node.right)
        assertThat(mapper.convertValue<PartialTree.Node>(json)).isEqualTo(node)
    }

    @Test(timeout=300_000)
	fun `complex PartialTree Node`() {
        val node = PartialTree.Node(
                left = PartialTree.IncludedLeaf(SecureHash.randomSHA256()),
                right = PartialTree.Node(
                        left = PartialTree.Leaf(SecureHash.randomSHA256()),
                        right = PartialTree.Leaf(SecureHash.randomSHA256())
                )
        )
        val json = mapper.valueToTree<ObjectNode>(node)
        println(mapper.writeValueAsString(json))
        assertThat(mapper.convertValue<PartialTree.Node>(json)).isEqualTo(node)
    }

    // TODO Issued
    // TODO PartyAndReference

    @Test(timeout=300_000)
	fun CordaX500Name() {
        testToStringSerialisation(CordaX500Name(
                commonName = "COMMON",
                organisationUnit = "ORG UNIT",
                organisation = "ORG",
                locality = "NYC",
                state = "NY",
                country = "US"
        ))
    }

    @Test(timeout=300_000)
	fun `SecureHash SHA256`() {
        testToStringSerialisation(SecureHash.randomSHA256())
    }

    @Test(timeout=300_000)
	fun NetworkHostAndPort() {
        testToStringSerialisation(NetworkHostAndPort("localhost", 9090))
    }

    @Test(timeout=300_000)
	fun UUID() {
        testToStringSerialisation(UUID.randomUUID())
    }

    @Test(timeout=300_000)
	fun Instant() {
        testToStringSerialisation(Instant.now())
    }

    @Test(timeout=300_000)
	fun `Date is treated as Instant`() {
        val date = Date()
        val json = mapper.valueToTree<TextNode>(date)
        assertThat(json.textValue()).isEqualTo(date.toInstant().toString())
        assertThat(mapper.convertValue<Date>(json)).isEqualTo(date)
    }

    @Test(timeout=300_000)
	fun `Party serialization`() {
        val json = mapper.valueToTree<TextNode>(MINI_CORP.party)
        assertThat(json.textValue()).isEqualTo(MINI_CORP.name.toString())
    }

    @Test(timeout=300_000)
	fun `Party serialization with isFullParty = true`() {
        partyObjectMapper.isFullParties = true
        val json = mapper.valueToTree<ObjectNode>(MINI_CORP.party)
        val (name, owningKey) = json.assertHasOnlyFields("name", "owningKey")
        assertThat(name.valueAs<CordaX500Name>(mapper)).isEqualTo(MINI_CORP.name)
        assertThat(owningKey.valueAs<PublicKey>(mapper)).isEqualTo(MINI_CORP.publicKey)
    }

    @Test(timeout=300_000)
	fun `Party deserialization on full name`() {
        fun convertToParty() = mapper.convertValue<Party>(TextNode(MINI_CORP.name.toString()))

        // Check that it fails if it can't find the party
        assertThatThrownBy { convertToParty() }

        partyObjectMapper.identities += MINI_CORP.party
        assertThat(convertToParty()).isEqualTo(MINI_CORP.party)
    }

    @Test(timeout=300_000)
	fun `Party deserialization on part of name`() {
        fun convertToParty() = mapper.convertValue<Party>(TextNode(MINI_CORP.name.organisation))

        // Check that it fails if it can't find the party
        assertThatThrownBy { convertToParty() }

        partyObjectMapper.identities += MINI_CORP.party
        assertThat(convertToParty()).isEqualTo(MINI_CORP.party)
    }

    @Test(timeout=300_000)
	fun `Party deserialization on public key`() {
        fun convertToParty() = mapper.convertValue<Party>(TextNode(MINI_CORP.publicKey.toBase58String()))

        // Check that it fails if it can't find the party
        assertThatThrownBy { convertToParty() }

        partyObjectMapper.identities += MINI_CORP.party
        assertThat(convertToParty()).isEqualTo(MINI_CORP.party)
    }

    @Test(timeout=300_000)
	fun `Party deserialization on name and key`() {
        val party = mapper.convertValue<Party>(mapOf(
                "name" to MINI_CORP.name,
                "owningKey" to MINI_CORP.publicKey
        ))
        // Party.equals is only defined on the public key so we must check the name as well
        assertThat(party.name).isEqualTo(MINI_CORP.name)
        assertThat(party.owningKey).isEqualTo(MINI_CORP.publicKey)
    }

    @Test(timeout=300_000)
	fun PublicKey() {
        val json = mapper.valueToTree<TextNode>(MINI_CORP.publicKey)
        assertThat(json.textValue()).isEqualTo(MINI_CORP.publicKey.toBase58String())
        assertThat(mapper.convertValue<PublicKey>(json)).isEqualTo(MINI_CORP.publicKey)
    }

    @Test(timeout=300_000)
	fun `EdDSA public key`() {
        val publicKey = Crypto.deriveKeyPairFromEntropy(Crypto.EDDSA_ED25519_SHA512, SEED).public
        val json = mapper.valueToTree<TextNode>(publicKey)
        assertThat(json.textValue()).isEqualTo(publicKey.toBase58String())
        assertThat(mapper.convertValue<PublicKey>(json)).isEqualTo(publicKey)
    }

    @Test(timeout=300_000)
	fun CompositeKey() {
        val innerKeys = (1..3).map { i ->
            Crypto.deriveKeyPairFromEntropy(Crypto.EDDSA_ED25519_SHA512, SEED + i.toBigInteger()).public
        }
        // Build a 2 of 3 composite key
        val publicKey = CompositeKey.Builder().let {
            innerKeys.forEach { key -> it.addKey(key, 1) }
            it.build(2)
        }
        val json = mapper.valueToTree<TextNode>(publicKey)
        assertThat(json.textValue()).isEqualTo(publicKey.toBase58String())
        assertThat(mapper.convertValue<CompositeKey>(json)).isEqualTo(publicKey)
    }

    @Test(timeout=300_000)
	fun AnonymousParty() {
        val anonymousParty = AnonymousParty(ALICE_PUBKEY)
        val json = mapper.valueToTree<TextNode>(anonymousParty)
        assertThat(json.textValue()).isEqualTo(ALICE_PUBKEY.toBase58String())
        assertThat(mapper.convertValue<AnonymousParty>(json)).isEqualTo(anonymousParty)
    }

    @Test(timeout=300_000)
	fun `PartyAndCertificate serialization`() {
        val json = mapper.valueToTree<TextNode>(MINI_CORP.identity)
        assertThat(json.textValue()).isEqualTo(MINI_CORP.name.toString())
    }

    @Test(timeout=300_000)
	fun `PartyAndCertificate serialization with isFullParty = true`() {
        partyObjectMapper.isFullParties = true
        val json = mapper.valueToTree<ObjectNode>(MINI_CORP.identity)
        println(mapper.writeValueAsString(json))
        val (name, certPath) = json.assertHasOnlyFields("name", "certPath")
        assertThat(name.valueAs<CordaX500Name>(mapper)).isEqualTo(MINI_CORP.name)
        assertThat(certPath.valueAs<CertPath>(mapper)).isEqualTo(MINI_CORP.identity.certPath)
    }

    @Test(timeout=300_000)
	fun `PartyAndCertificate deserialization on cert path`() {
        val certPathJson = mapper.valueToTree<JsonNode>(MINI_CORP.identity.certPath)
        val partyAndCert = mapper.convertValue<PartyAndCertificate>(mapOf("certPath" to certPathJson))
        // PartyAndCertificate.equals is defined on the Party so we must check the certPath directly
        assertThat(partyAndCert.certPath).isEqualTo(MINI_CORP.identity.certPath)
    }

    @Test(timeout=300_000)
	fun `NodeInfo serialization`() {
        val (nodeInfo) = createNodeInfoAndSigned(ALICE_NAME)
        val json = mapper.valueToTree<ObjectNode>(nodeInfo)
        val (addresses, legalIdentitiesAndCerts, platformVersion, serial) = json.assertHasOnlyFields(
                "addresses",
                "legalIdentitiesAndCerts",
                "platformVersion",
                "serial"
        )
        addresses.run {
            assertThat(this).hasSize(1)
            assertThat(this[0].valueAs<NetworkHostAndPort>(mapper)).isEqualTo(nodeInfo.addresses[0])
        }
        legalIdentitiesAndCerts.run {
            assertThat(this).hasSize(1)
            assertThat(this[0].valueAs<CordaX500Name>(mapper)).isEqualTo(ALICE_NAME)
        }
        assertThat(platformVersion.intValue()).isEqualTo(nodeInfo.platformVersion)
        assertThat(serial.longValue()).isEqualTo(nodeInfo.serial)
    }

    @Test(timeout=300_000)
	fun `NodeInfo deserialization on name`() {
        val (nodeInfo) = createNodeInfoAndSigned(ALICE_NAME)

        fun convertToNodeInfo() = mapper.convertValue<NodeInfo>(TextNode(ALICE_NAME.toString()))

        assertThatThrownBy { convertToNodeInfo() }

        partyObjectMapper.identities += nodeInfo.legalIdentities
        partyObjectMapper.nodes += nodeInfo
        assertThat(convertToNodeInfo()).isEqualTo(nodeInfo)
    }

    @Test(timeout=300_000)
	fun `NodeInfo deserialization on public key`() {
        val (nodeInfo) = createNodeInfoAndSigned(ALICE_NAME)

        fun convertToNodeInfo() = mapper.convertValue<NodeInfo>(TextNode(nodeInfo.legalIdentities[0].owningKey.toBase58String()))

        assertThatThrownBy { convertToNodeInfo() }

        partyObjectMapper.identities += nodeInfo.legalIdentities
        partyObjectMapper.nodes += nodeInfo
        assertThat(convertToNodeInfo()).isEqualTo(nodeInfo)
    }

    @Test(timeout=300_000)
	fun CertPath() {
        val certPath = MINI_CORP.identity.certPath
        val json = mapper.valueToTree<ObjectNode>(certPath)
        println(mapper.writeValueAsString(json))
        val (type, certificates) = json.assertHasOnlyFields("type", "certificates")
        assertThat(type.textValue()).isEqualTo(certPath.type)
        certificates.run {
            val serialNumbers = elements().asSequence().map { it["serialNumber"].bigIntegerValue() }.toList()
            assertThat(serialNumbers).isEqualTo(certPath.x509Certificates.map { it.serialNumber })
        }
        assertThat(mapper.convertValue<CertPath>(json).encoded).isEqualTo(certPath.encoded)
    }

    @Test(timeout=300_000)
	fun `X509Certificate serialization`() {
        val cert: X509Certificate = MINI_CORP.identity.certificate
        val json = mapper.valueToTree<ObjectNode>(cert)
        println(mapper.writeValueAsString(json))
        assertThat(json["serialNumber"].bigIntegerValue()).isEqualTo(cert.serialNumber)
        assertThat(json["issuer"].valueAs<X500Principal>(mapper)).isEqualTo(cert.issuerX500Principal)
        assertThat(json["subject"].valueAs<X500Principal>(mapper)).isEqualTo(cert.subjectX500Principal)
        // cert.publicKey should be converted to a supported format (this is required because [Certificate] returns keys as SUN EC keys, not BC).
        assertThat(json["publicKey"].valueAs<PublicKey>(mapper)).isEqualTo(Crypto.toSupportedPublicKey(cert.publicKey))
        assertThat(json["notAfter"].valueAs<Date>(mapper)).isEqualTo(cert.notAfter)
        assertThat(json["notBefore"].valueAs<Date>(mapper)).isEqualTo(cert.notBefore)
        assertThat(json["encoded"].binaryValue()).isEqualTo(cert.encoded)
    }

    @Test(timeout=300_000)
	fun `X509Certificate serialization when extendedKeyUsage is null`() {
        val cert: X509Certificate = spy(MINI_CORP.identity.certificate)
        whenever(cert.extendedKeyUsage).thenReturn(null)
        // should work even if extendedKeyUsage is null
        mapper.valueToTree<ObjectNode>(cert)
    }

    @Test(timeout=300_000)
	fun `X509Certificate deserialization`() {
        val cert: X509Certificate = MINI_CORP.identity.certificate
        assertThat(mapper.convertValue<X509Certificate>(mapOf("encoded" to cert.encoded))).isEqualTo(cert)
        assertThat(mapper.convertValue<X509Certificate>(BinaryNode(cert.encoded))).isEqualTo(cert)
    }

    @Test(timeout=300_000)
	fun X500Principal() {
        testToStringSerialisation(X500Principal("CN=Common,L=London,O=Org,C=UK"))
    }

    @Test(timeout=300_000)
	fun `@CordaSerializable class which has non-c'tor properties`() {
        val data = NonCtorPropertiesData(4434)
        val json = mapper.valueToTree<ObjectNode>(data)
        val (value) = json.assertHasOnlyFields("value")
        assertThat(value.intValue()).isEqualTo(4434)
        assertThat(mapper.convertValue<NonCtorPropertiesData>(json)).isEqualTo(data)
    }

//    @Test(timeout=300_000)
//	fun `LinearState where the linearId property does not match the backing field`() {
//        val funkyLinearState = FunkyLinearState(UniqueIdentifier())
//        // As a sanity check, show that this is a valid CordaSerializable class
//        assertThat(funkyLinearState.serialize().deserialize()).isEqualTo(funkyLinearState)
//        val json = mapper.valueToTree<ObjectNode>(funkyLinearState)
//        assertThat(mapper.convertValue<FunkyLinearState>(json)).isEqualTo(funkyLinearState)
//    }

    @Test(timeout=300_000)
	fun `kotlin object`() {
        val json = mapper.valueToTree<ObjectNode>(KotlinObject)
        assertThat(mapper.convertValue<KotlinObject>(json)).isSameAs(KotlinObject)
    }

    @Test(timeout=300_000)
	fun `@CordaSerializable kotlin object`() {
        val json = mapper.valueToTree<ObjectNode>(CordaSerializableKotlinObject)
        assertThat(mapper.convertValue<CordaSerializableKotlinObject>(json)).isSameAs(CordaSerializableKotlinObject)
    }

    private inline fun <reified T : Any> testToStringSerialisation(value: T) {
        val json = mapper.valueToTree<TextNode>(value)
        assertThat(json.textValue()).isEqualTo(value.toString())
        assertThat(mapper.convertValue<T>(json)).isEqualTo(value)
    }

    private fun JsonNode.assertHasOnlyFields(vararg fieldNames: String): List<JsonNode> {
        assertThat(fieldNames()).toIterable().containsOnly(*fieldNames)
        return fieldNames.map { this[it] }
    }

    @CordaSerializable
    private data class TestData(val name: CordaX500Name, val summary: String, val subData: SubTestData)

    @CordaSerializable
    private data class SubTestData(val value: Int)

    @CordaSerializable
    private data class NonCtorPropertiesData(val value: Int) {
        @Suppress("unused")
        val nonCtor: Int get() = value
    }

//    private data class FunkyLinearState(private val linearID: UniqueIdentifier) : LinearState {
//        override val linearId: UniqueIdentifier get() = linearID
//        override val participants: List<AbstractParty> get() = emptyList()
//    }

    private object KotlinObject

    @CordaSerializable
    private object CordaSerializableKotlinObject

    private class TestPartyObjectMapper : JacksonSupport.PartyObjectMapper {
        override var isFullParties: Boolean = false
        val identities = ArrayList<Party>()
        val nodes = ArrayList<NodeInfo>()
        override fun wellKnownPartyFromX500Name(name: CordaX500Name): Party? {
            return identities.find { it.name == name }
        }
        override fun partyFromKey(owningKey: PublicKey): Party? {
            return identities.find { it.owningKey == owningKey }
        }
        override fun partiesFromName(query: String): Set<Party> {
            return identities.filter { query in it.name.toString() }.toSet()
        }
        override fun nodeInfoFromParty(party: AbstractParty): NodeInfo? {
            return nodes.find { party in it.legalIdentities }
        }
    }
}
