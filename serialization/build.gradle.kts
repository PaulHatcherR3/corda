plugins {
    id("org.ajoberstar.grgit") version "4.0.2"

    id("com.jfrog.artifactory") version "4.17.2"

    `maven-publish`

    `java-library`
}

repositories {
    val cordaUseCache = System.getenv("CORDA_USE_CACHE")
    if(cordaUseCache != null) {
        maven(url = "${properties["artifactory_contextUrl"]}/$cordaUseCache") {
            name = "R3 Maven remote repositories"
        }
    } else {
        maven(url = "${properties["artifactory_contextUrl"]}/corda-dependencies-dev")
        maven(url = "${properties["artifactory_contextUrl"]}/corda-releases")
        maven(url = "${properties["artifactory_contextUrl"]}/corda-dependencies") {
            metadataSources {
                mavenPom()
                // TODO: Because Gradle 6.1 will not look for the jar if there is no pom file (https://docs.gradle.org/current/userguide/declaring_dependencies.html#sec:dependency-types)
                //       We need to define this.
                //       Ideally we will have a pom file in https://software.r3.com/artifactory/webapp/#/artifacts/browse/tree/General/corda-dependencies/com/github/bft-smart/library/master-v1.1-beta-g6215ec8-87
                //       Or, even better, we should either own this (and make a proper release) or use something from Maven.
                artifact()
            }
        }
        maven(url = "${properties["artifactory_contextUrl"]}/corda-dev")
        maven(url = "${properties["artifactory_contextUrl"]}/testing-ci-uploads-dev-local") {
            credentials {
                username = System.getenv("CORDA_ARTIFACTORY_USERNAME")
                password = System.getenv("CORDA_ARTIFACTORY_PASSWORD")
            }
        }
        mavenCentral()
        jcenter()
        maven(url = "https://kotlin.bintray.com/kotlinx")
        maven(url = "https://repo.gradle.org/gradle/libs-releases-local/")
        mavenLocal()
    }
}

val baseVersion = properties["cordaVersion"]
val versionSuffix = properties["versionSuffix"]
val releaseVersion = if(versionSuffix != null) {
    "$baseVersion-$versionSuffix"
} else {
    "$baseVersion"
}

val branchName = grgit.branch.current().name
val revision = grgit.head().id
val tagName = grgit.tag.list().firstOrNull {
    it.commit.id == revision
}?.name

if (tagName != null && tagName.startsWith("release-os-")){
    version = tagName.substringAfter("release-os-")
} else if(branchName.startsWith("release/os/")) {
    version = branchName.substringAfter("release/os/") + "-SNAPSHOT"
} else {
    val ticketRegex = Regex("[a-z0-9_]+/(.+)/.+")
    val ticket = ticketRegex.matchEntire(branchName)
    version = ticket?.groupValues?.get(1)?.toUpperCase() ?: "$baseVersion-$revision"
}

dependencies {
    implementation(project(":core"))

    implementation("com.google.guava:guava:${properties["guavaVersion"]}")
    implementation("org.ow2.asm:asm:${properties["asm_version"]}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${properties["kotlinVersion"]}")
    implementation("org.apache.qpid:proton-j:${properties["protonj_version"]}")
    implementation("org.slf4j:slf4j-api:${properties["slf4j_version"]}")
    implementation("org.apache.activemq:artemis-commons:${properties["artemis_version"]}")
    implementation("io.reactivex:rxjava:${properties["rxjava_version"]}")
    implementation("org.iq80.snappy:snappy:${properties["snappy_version"]}")

    testImplementation("junit:junit:${properties["junit_version"]}")
    testImplementation("org.jetbrains.kotlin:kotlin-test:${properties["kotlinVersion"]}")
    testImplementation("org.assertj:assertj-core:${properties["assertj_version"]}")
    testImplementation("com.nhaarman:mockito-kotlin:${properties["mockito_kotlin_version"]}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${properties["junit_jupiter_version"]}")
    testImplementation("org.hamcrest:hamcrest-library:${properties["hamcrest_version"]}")
    testImplementation("com.fasterxml.jackson.core:jackson-databind:${properties["jackson_version"]}")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().forEach { compileKotlin ->
    compileKotlin.kotlinOptions.allWarningsAsErrors = true
    compileKotlin.kotlinOptions.verbose = true
    compileKotlin.kotlinOptions.jvmTarget = "11"
    compileKotlin.kotlinOptions.freeCompilerArgs += "-Xjvm-default=compatibility"
    compileKotlin.kotlinOptions.freeCompilerArgs += "-java-parameters"
}
tasks.withType<JavaCompile>().forEach { compileJava ->
    compileJava.options.compilerArgs.add("-parameters")
}

tasks.withType<Jar>().forEach { task ->
    task.manifest {
        attributes("Corda-Release-Version" to version)
        attributes("Corda-Platform-Version" to properties["platformVersion"])
        attributes("Corda-Revision" to revision)
        attributes("Corda-Vendor" to "Corda Open Source")
        attributes("Automatic-Module-Name" to "net.corda.${task.project.name.replace('-', '.')}")
        attributes("Corda-Docs-Link" to "https://docs.corda.net/docs/corda-os/$baseVersion")
    }
}

val javaTestCompiler = tasks.getByName("compileTestJava") as JavaCompile
javaTestCompiler.options.compilerArgs.addAll(listOf("--add-exports",
        "java.base/sun.security.x509=ALL-UNNAMED",
        "--add-exports",
        "java.base/sun.security.util=ALL-UNNAMED"))

val libGroupId = "net.corda"
val libArtifactId = "serialisation"

tasks.withType<Jar>().forEach { task ->
    task.manifest {
        attributes("Corda-Release-Version" to revision)
        attributes("Corda-Platform-Version" to properties["platformVersion"])
        attributes("Corda-Revision" to revision)
        attributes("Corda-Vendor" to "Corda Open Source")
        attributes("Automatic-Module-Name" to "net.corda.${task.project.name.replace('-', '.')}")
        attributes("Corda-Docs-Link" to "https://docs.corda.net/docs/corda-os/$baseVersion")
    }
}