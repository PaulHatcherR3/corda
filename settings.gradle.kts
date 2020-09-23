rootProject.name = "corda-project"

// Projects that have been left out of the build

//  Moving to a different repo
//  - client:jfx
//  - client:mock
//  - samples:*

//  Only required for integration tests
//  - node-driver
//  - smoke-test-utils
//  - test-cli
//  - test-db
//  - testing:cordapps:dbfailure:*

//  Not relevant to the main build
//  - detekt-plugins
//  - docker
//  - docs
//  - isolated
//  - tools:explorer
//  - tools:explorer:capsule
//  - tools:demobench
//  - tools:loadtest
//  - tools:graphs
//  - tools:bootstrapper
//  - tools:blobinspector
//  - tools:shell-cli
//  - tools:network-builder
//  - tools:worldmap
//  - tools:checkpoint-agent
//  - tools:error-tool

//  Not relevant to Corda5
//  - experimental:*
//  - jdk8u-deterministic


include("client:jackson")
include("client:rpc")
include("confidential-identities")
include("core")
include("core-deterministic")
include("core-test-utils")
include("core-tests")
include("common:validation")
include("common:configuration-parsing")
include("common:logging")
include("finance:contracts")
include("finance:workflows")
include("node")
include("node-api")
include("node:djvm")
include("serialization")
include("serialization-deterministic")
include("serialization-djvm")
include("serialization-djvm:deserializers")
include("test-common")
include("test-utils")
arrayOf("test-common", "core-test-utils", "test-utils").forEach {
    print(File("$settingsDir/testing/$it"))
    project(":$it").projectDir = File("$settingsDir/testing/$it")
}

include("tools:cliutils")
include("tools:shell")
