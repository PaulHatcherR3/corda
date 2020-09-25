import org.gradle.api.tasks.testing.logging.TestLogEvent

val libGroupId = "net.corda"

plugins {
    kotlin("jvm") version "1.4.0"

    // TODO: 1.13.0 was published today!. once jcentral get synch we should change the version
    //       and set   warningsAsErrors: false in the config
    id("io.gitlab.arturbosch.detekt") version "1.13.0"
    id("org.ajoberstar.grgit") version "4.0.2"

    id("com.jfrog.artifactory") version "4.17.2"

    `maven-publish`

    `java-library`
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.12.0")
}

allprojects {
    repositories {
        val cordaUseCache = System.getenv("CORDA_USE_CACHE")
        if(cordaUseCache != null) {
            maven(url = "${properties["artifactoryContextUrl"]}/$cordaUseCache") {
                name = "R3 Maven remote repositories"
            }
        } else {
            maven(url = "${properties["artifactoryContextUrl"]}/corda-dependencies-dev")
            maven(url = "${properties["artifactoryContextUrl"]}/corda-releases")
            maven(url = "${properties["artifactoryContextUrl"]}/corda-dependencies") {
                metadataSources {
                    mavenPom()
                    // TODO: Because Gradle 6.1 will not look for the jar if there is no pom file (https://docs.gradle.org/current/userguide/declaring_dependencies.html#sec:dependency-types)
                    //       We need to define this.
                    //       Ideally we will have a pom file in https://software.r3.com/artifactory/webapp/#/artifacts/browse/tree/General/corda-dependencies/com/github/bft-smart/library/master-v1.1-beta-g6215ec8-87
                    //       Or, even better, we should either own this (and make a proper release) or use something from Maven.
                    artifact()
                }
            }
            maven(url = "${properties["artifactoryContextUrl"]}/corda-dev")
            maven(url = "${properties["artifactoryContextUrl"]}/testing-ci-uploads-dev-local") {
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
}

detekt {
    config = files("$projectDir/detekt-config.yml, " +
            "$projectDir/detekt-baseline-config.yml")
    baseline = file("$projectDir/detekt-baseline.xml")
    buildUponDefaultConfig = true
}

subprojects {

    apply(plugin = "kotlin")

    dependencies {
        implementation(kotlin("stdlib"))
        implementation("org.jetbrains.kotlin:kotlin-reflect:${properties["kotlinVersion"]}")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${properties["kotlinVersion"]}")

        testImplementation("org.jetbrains.kotlin:kotlin-test:${properties["kotlinVersion"]}")
        testImplementation("org.mockito:mockito-core:${properties["mockitoVersion"]}")
        testImplementation("com.nhaarman:mockito-kotlin:${properties["mockitoKotlinVersion"]}")
        testImplementation("org.junit.jupiter:junit-jupiter:${properties["junitJupiterVersion"]}")
        
        testRuntimeOnly("org.junit.platform:junit-platform-launcher:${properties["junitPlatformVersion"]}")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${properties["junitJupiterVersion"]}")
        testRuntimeOnly("org.junit.vintage:junit-vintage-engine:${properties["junitVintageVersion"]}")
    }

    val baseVersion = properties["cordaVersion"]

    version = "${baseVersion}-${properties["versionSuffix"]}"

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

    // Added to support junit5 tests
    tasks.withType<Test>{
        useJUnitPlatform()
        testLogging {
            info.events = mutableSetOf(TestLogEvent.FAILED, TestLogEvent.PASSED, TestLogEvent.SKIPPED)
        }

    }

    tasks.withType<Jar>().forEach { task ->
        task.manifest {
            attributes("Corda-Release-Version" to version)
            attributes("Corda-Platform-Version" to properties["platformVersion"])
            // attributes("Corda-Revision" to revision)
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
}

detekt {
    config = files("$projectDir/detekt-config.yml, " +
            "$projectDir/detekt-baseline-config.yml")
    baseline = file("$projectDir/detekt-baseline.xml")
    buildUponDefaultConfig = true
}
