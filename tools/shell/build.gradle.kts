dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${properties["kotlinVersion"]}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${properties["kotlinVersion"]}")
    implementation("com.google.guava:guava:${properties["guavaVersion"]}")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${properties["jackson_version"]}")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:${properties["jackson_version"]}")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${properties["jackson_version"]}")
    implementation("org.apache.logging.log4j:log4j-core:${properties["log4j_version"]}")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:${properties["log4j_version"]}")
    implementation("org.slf4j:slf4j-api:${properties["slf4j_version"]}")
    implementation("org.apache.commons:commons-lang3:${properties["commons_lang_version"]}")
    implementation("org.apache.activemq:artemis-core-client:${properties["artemis_version"]}")
    implementation("org.apache.activemq:artemis-server:${properties["artemis_version"]}")
    implementation("org.apache.activemq:artemis-amqp-protocol:${properties["artemis_version"]}")
    implementation("io.reactivex:rxjava:${properties["rxjava_version"]}")
    implementation("javax.persistence:javax.persistence-api:${properties["persistence-api_version"]}")

    implementation("org.fusesource.jansi:jansi:${properties["jansi_version"]}")

    implementation("org.crashub:crash.shell:${properties["crash_version"]}")
    implementation("org.crashub:crash.connectors.ssh:${properties["crash_version"]}")

    implementation(project(":client:jackson"))
    implementation(project(":client:rpc"))
    implementation(project(":core"))
    implementation(project(":node-api"))
}