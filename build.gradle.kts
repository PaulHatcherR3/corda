import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.api.tasks.testing.logging.TestLogEvent
import io.gitlab.arturbosch.detekt.DetektPlugin

val libGroupId = "net.corda"
val rootProjectDir = rootDir


plugins {
    kotlin("jvm") version "1.4.0"
    id("io.gitlab.arturbosch.detekt") version "1.14.0"
    id("org.ajoberstar.grgit") version "4.0.2" // used for GIT interaction (e.g. extract commit hash)
    id("com.jfrog.artifactory") version "4.17.2"
    `maven-publish`
    `java-library`

    id("com.dorongold.task-tree") version "1.5" // utility to visualise Gradle task DAG
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

// TODO: note sure where the right place is for this.
val platformVersion = properties["platformVersion"]
val baseVersion = properties["cordaVersion"]
val packageVersion = "${baseVersion}-${properties["versionSuffix"]}"

println("Building:")
println("platformVersion: $platformVersion")
println("baseVersion: $baseVersion")
println("packageVersion: ${packageVersion}")

subprojects {
    apply(plugin = "kotlin")
    apply<DetektPlugin>()

    // NOTE: question whether it is "ok" to force dependencies on all modules like this
    //  thinking is that for the test dependencies it's ok as it'll keep things consistent.
    //  we can add exclusions, or review this if necessary.
    dependencies {
        // Test libraries -> keep consistent across modules
        testImplementation("org.jetbrains.kotlin:kotlin-test:${properties["kotlinVersion"]}")
        testImplementation("org.mockito:mockito-core:${properties["mockitoVersion"]}")
        testImplementation("com.nhaarman:mockito-kotlin:${properties["mockitoKotlinVersion"]}")
        testImplementation("org.junit.jupiter:junit-jupiter:${properties["junitJupiterVersion"]}")

        // Test runtime libraries -> also keep consistent
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${properties["junitJupiterVersion"]}")
        // TODO: remove vintage engine when existing tests labelled "legacy" and we don't have a dependency on 4 anymore
        testRuntimeOnly("org.junit.vintage:junit-vintage-engine:${properties["junitVintageVersion"]}")

        detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.14.0")
    }

    tasks {
        withType<KotlinCompile>().configureEach {
            kotlinOptions {
                allWarningsAsErrors = true
                verbose = true
                jvmTarget = "11"
                freeCompilerArgs += "-Xjvm-default=compatibility"
                freeCompilerArgs += "-java-parameters"
            }
        }

        withType<JavaCompile>().configureEach {
            val compilerArgs = options.compilerArgs
            compilerArgs.add("-parameters")
        }

        // TODO: does this really need to apply to all modules or can this be moved to the modules that need it only?
        named<JavaCompile>("compileTestJava") {
            val compilerArgs = options.compilerArgs
            compilerArgs.add("--add-exports")
            compilerArgs.add("java.base/sun.security.x509=ALL-UNNAMED")
            compilerArgs.add("--add-exports")
            compilerArgs.add("java.base/sun.security.util=ALL-UNNAMED")
        }

        // TODO: as above, this may not apply to all modules, so maybe should be moved out
        withType<Jar>().configureEach {
            manifest {
                attributes("Corda-Release-Version" to packageVersion)
                attributes("Corda-Platform-Version" to platformVersion)
                // TODO: review this when looking at packaging & publishing
                // attributes("Corda-Revision" to revision)
                attributes("Corda-Vendor" to "Corda Open Source")
                attributes("Automatic-Module-Name" to "net.corda.${project.name.replace('-', '.')}")
                attributes("Corda-Docs-Link" to "https://docs.corda.net/docs/corda-os/$baseVersion")
            }
        }

        // Added to support junit5 tests
        withType<Test>().configureEach {
            useJUnitPlatform()
            testLogging {
                info.events = mutableSetOf(TestLogEvent.FAILED, TestLogEvent.PASSED, TestLogEvent.SKIPPED)
            }

        }

        detekt {
            buildUponDefaultConfig = true
            baseline = file("$projectDir/detekt-baseline.xml")
            config = files("$rootProjectDir/detekt-config.yml")
            parallel = true
            reports {
                xml {
                    enabled = true
                    destination = file("$projectDir/build/detekt-report.xml")
                }
                html {
                    enabled = false
                }
                txt {
                    enabled = false
                }
            }
        }
    }
}
