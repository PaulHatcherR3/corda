dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${properties["kotlinVersion"]}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${properties["kotlinVersion"]}")
    implementation("com.google.guava:guava:${properties["guavaVersion"]}")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${properties["jacksonVersion"]}")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:${properties["jacksonVersion"]}")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${properties["jacksonVersion"]}")
    implementation("org.apache.logging.log4j:log4j-core:${properties["log4jVersion"]}")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:${properties["log4jVersion"]}")
    implementation("org.slf4j:slf4j-api:${properties["slf4jVersion"]}")
    implementation("org.apache.commons:commons-lang3:${properties["commonsLangVersion"]}")
    implementation("org.apache.activemq:artemis-core-client:${properties["artemisVersion"]}")
    implementation("org.apache.activemq:artemis-server:${properties["artemisVersion"]}")
    implementation("org.apache.activemq:artemis-amqp-protocol:${properties["artemisVersion"]}")
    implementation("io.reactivex:rxjava:${properties["rxjavaVersion"]}")
    implementation("javax.persistence:javax.persistence-api:${properties["persistence-apiVersion"]}")
    implementation("org.fusesource.jansi:jansi:${properties["jansiVersion"]}")
    implementation("org.crashub:crash.shell:${properties["crashVersion"]}")
    implementation("org.crashub:crash.connectors.ssh:${properties["crashVersion"]}")

    implementation(project(":client:jackson"))
    implementation(project(":client:rpc"))
    implementation(project(":core"))
    implementation(project(":node-api"))

    testImplementation("org.jetbrains.kotlin:kotlin-test:${properties["kotlinVersion"]}")
    testImplementation("org.mockito:mockito-core:${properties["mockitoVersion"]}")
    testImplementation("com.nhaarman:mockito-kotlin:${properties["mockitoKotlinVersion"]}")
    testImplementation("junit:junit:${properties["junitVersion"]}")
    testImplementation("org.assertj:assertj-core:${properties["assertjVersion"]}")

    testImplementation(project(":core-test-utils"))
    testImplementation(project(":node"))
    testImplementation(project(":test-common"))
    testImplementation(project(":test-utils"))
}