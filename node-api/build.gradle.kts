dependencies {
    // Kryo: object graph serialization.
    implementation("com.esotericsoftware:kryo:${properties["kryoVersion"]}")
    implementation("de.javakaffee:kryo-serializers:${properties["kryoSerializerVersion"]}")
    implementation("io.netty:netty-handler-proxy:${properties["nettyVersion"]}")
    implementation("com.google.guava:guava:${properties["guavaVersion"]}")
    // TypeSafe Config: for simple and human friendly config files.
    implementation("com.typesafe:config:${properties["typesafeConfigVersion"]}")
    implementation("org.apache.qpid:proton-j:${properties["protonjVersion"]}")
    // SQL connection pooling library
    implementation("com.zaxxer:HikariCP:${properties["hikariVersion"]}")
    // ClassGraph: classpath scanning
    implementation("io.github.classgraph:classgraph:${properties["classGraphVersion"]}")
    // For caches rather than guava
    implementation("com.github.ben-manes.caffeine:caffeine:${properties["caffeineVersion"]}")
    // For db migration
    implementation("org.liquibase:liquibase-core:${properties["liquibaseVersion"]}")
    implementation("com.fasterxml.jackson.core:jackson-databind:${properties["jacksonVersion"]}")
    implementation("org.apache.activemq:artemis-core-client:${properties["artemisVersion"]}")
    implementation("org.apache.activemq:artemis-commons:${properties["artemisVersion"]}")
    implementation("org.apache.activemq:artemis-amqp-protocol:${properties["artemisVersion"]}") {
        // Gains our proton-j version from core module.
        exclude("org.apache.qpid", "proton-j")
    }

    // JDK11: required by Quasar at run-time
    runtimeOnly("com.mattbertolini:liquibase-slf4j:${properties["liquibaseRuntimeVersion"]}")
    runtimeOnly("com.esotericsoftware:kryo:${properties["kryoVersion"]}")


    // COMMON
    implementation("javax.persistence:javax.persistence-api:${properties["persistenceApiVersion"]}")
    implementation("org.hibernate:hibernate-core:${properties["hibernateVersion"]}")
    implementation("io.reactivex:rxjava:${properties["rxjavaVersion"]}")
    implementation("co.paralleluniverse:quasar-core:${properties["quasarVersion"]}")
    implementation("org.bouncycastle:bcprov-jdk15on:${properties["bouncycastleVersion"]}")
    implementation("org.bouncycastle:bcpkix-jdk15on:${properties["bouncycastleVersion"]}")
    implementation("net.i2p.crypto:eddsa:${properties["eddsaVersion"]}")
    implementation("org.apache.commons:commons-lang3:${properties["commonsLangVersion"]}")

    implementation(project(":core"))
    implementation(project(":serialization"))
    implementation(project(":common:configuration-parsing"))
    implementation(project(":common:logging"))
    implementation(project(":common:validation"))

    testImplementation("org.assertj:assertj-core:${properties["assertjVersion"]}")
    testImplementation("junit:junit:${properties["junitVersion"]}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${properties["junitJupiterVersion"]}")

    testImplementation(project(":core-test-utils"))
    testImplementation(project(":test-common"))
    testImplementation(project(":test-utils"))

}