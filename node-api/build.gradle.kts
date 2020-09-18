dependencies {
    // Kryo: object graph serialization.
    implementation("com.esotericsoftware:kryo:${properties["kryo_version"]}")
    implementation("de.javakaffee:kryo-serializers:${properties["kryo_serializer_version"]}")
    implementation("io.netty:netty-handler-proxy:${properties["netty_version"]}")
    // TypeSafe Config: for simple and human friendly config files.
    implementation("com.typesafe:config:${properties["typesafe_config_version"]}")
    implementation("org.apache.qpid:proton-j:${properties["protonj_version"]}")
    // SQL connection pooling library
    implementation("com.zaxxer:HikariCP:${properties["hikari_version"]}")
    // ClassGraph: classpath scanning
    implementation("io.github.classgraph:classgraph:${properties["class_graph_version"]}")
    // For caches rather than guava
    implementation("com.github.ben-manes.caffeine:caffeine:${properties["caffeine_version"]}")
    // For db migration
    implementation("org.liquibase:liquibase-core:${properties["liquibase_version"]}")
    implementation("com.fasterxml.jackson.core:jackson-databind:${properties["jackson_version"]}")
    implementation("org.apache.activemq:artemis-core-client:${properties["artemis_version"]}")
    implementation("org.apache.activemq:artemis-commons:${properties["artemis_version"]}")
    implementation("org.apache.activemq:artemis-amqp-protocol:${properties["artemis_version"]}") {
        // Gains our proton-j version from core module.
        exclude("org.apache.qpid", "proton-j")
    }

    // JDK11: required by Quasar at run-time
    runtimeOnly("com.mattbertolini:liquibase-slf4j:${properties["runtime_liquibase_version"]}")
    runtimeOnly("com.esotericsoftware:kryo:${properties["kryo_version"]}")


    // COMMON
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${properties["kotlinVersion"]}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${properties["kotlinVersion"]}")
    implementation("org.jetbrains.kotlin:kotlin-test:${properties["kotlinVersion"]}")
    implementation("javax.persistence:javax.persistence-api:${properties["persistence-api_version"]}")
    implementation("org.hibernate:hibernate-core:${properties["hibernate_version"]}")
    implementation("io.reactivex:rxjava:${properties["rxjava_version"]}")
    implementation("co.paralleluniverse:quasar-core:${properties["quasarVersion11"]}")
    implementation("org.bouncycastle:bcprov-jdk15on:${properties["bouncycastleVersion"]}")
    implementation("org.bouncycastle:bcpkix-jdk15on:${properties["bouncycastleVersion"]}")
    implementation("net.i2p.crypto:eddsa:${properties["eddsa_version"]}")
    implementation("org.apache.commons:commons-lang3:${properties["commons_lang_version"]}")

    implementation(project(":core"))
    implementation(project(":serialization"))
    implementation(project(":common:configuration-parsing"))
    implementation(project(":common:logging"))
    implementation(project(":common:validation"))

    testImplementation("com.nhaarman:mockito-kotlin:${properties["mockito_kotlin_version"]}")
    testImplementation("org.assertj:assertj-core:${properties["assertj_version"]}")
    testImplementation("junit:junit:${properties["junit_version"]}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${properties["junit_jupiter_version"]}")

    testImplementation(project(":core-test-utils"))
    testImplementation(project(":test-common"))
    testImplementation(project(":test-utils"))

}