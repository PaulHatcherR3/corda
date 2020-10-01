dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:${properties["kotlinVersion"]}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${properties["kotlinVersion"]}")
    // Locked these versions together as it makes sense
    implementation("io.atomix.copycat:copycat-client:${properties["copycatVersion"]}")
    implementation("io.atomix.copycat:copycat-server:${properties["copycatVersion"]}")
    implementation("com.esotericsoftware:kryo:${properties["kryoVersion"]}")
    implementation("org.hibernate:hibernate-core:${properties["hibernateVersion"]}")
    implementation("io.dropwizard.metrics:metrics-jmx:${properties["metricsVersion"]}")
    implementation("com.google.guava:guava:${properties["guavaVersion"]}")
    implementation("javax.persistence:javax.persistence-api:${properties["persistenceApiVersion"]}")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:${properties["log4jVersion"]}")
    implementation("org.apache.logging.log4j:log4j-web:${properties["log4jVersion"]}")
    implementation("com.github.ben-manes.caffeine:caffeine:${properties["caffeineVersion"]}")
    implementation("co.paralleluniverse:quasar-core:${properties["quasarVersion"]}")
    implementation("io.reactivex:rxjava:${properties["rxjavaVersion"]}")
    implementation("org.apache.activemq:artemis-core-client:${properties["artemisVersion"]}")
    implementation("org.apache.activemq:artemis-server:${properties["artemisVersion"]}")
    implementation("org.apache.activemq:artemis-amqp-protocol:${properties["artemisVersion"]}")
    implementation("org.apache.commons:commons-lang3:${properties["commonsLangVersion"]}")
    implementation("org.bouncycastle:bcprov-jdk15on:${properties["bouncycastleVersion"]}")
    implementation("org.bouncycastle:bcpkix-jdk15on:${properties["bouncycastleVersion"]}")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${properties["jacksonVersion"]}")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:${properties["jacksonVersion"]}")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${properties["jacksonVersion"]}")
    implementation("com.typesafe:config:${properties["typesafe_configVersion"]}")
    implementation("org.liquibase:liquibase-core:${properties["liquibaseVersion"]}")
    implementation("org.apache.shiro:shiro-core:${properties["shiroVersion"]}")
    implementation("io.github.classgraph:classgraph:${properties["classgraphVersion"]}")
    implementation("org.jolokia:jolokia-jvm:${properties["jolokiaVersion"]}:agent")
    implementation("info.picocli:picocli:${properties["picocliVersion"]}")
    implementation("com.zaxxer:HikariCP:${properties["hikariVersion"]}")
    implementation("org.crashub:crash.shell:${properties["crashVersion"]}")
    implementation("org.crashub:crash.connectors.ssh:${properties["crashVersion"]}")
    implementation("com.h2database:h2:${properties["h2Version"]}")
    implementation("com.palominolabs.metrics:metrics-new-relic:${properties["metricsNewRelicVersion"]}")
    implementation("com.jcabi:jcabi-manifests:${properties["jcabiManifestsVersion"]}")
    implementation("org.slf4j:jul-to-slf4j:${properties["slf4jVersion"]}")
    implementation("org.fusesource.jansi:jansi:${properties["jansiVersion"]}")
    implementation("com.squareup.okhttp3:okhttp:${properties["okhttpVersion"]}")
    implementation("com.github.bft-smart:library:${properties["bftSmartVersion"]}")
    implementation("io.atomix.catalyst:catalyst-netty:${properties["catalystVersion"]}")
    implementation("com.lmax:disruptor:${properties["disruptorVersion"]}")

    implementation(project(":client:jackson"))
    implementation(project(":client:rpc"))
    implementation(project(":confidential-identities"))
    implementation(project(":common:configuration-parsing"))
    implementation(project(":common:logging"))
    implementation(project(":common:validation"))
    implementation(project(":core"))
    implementation(project(":core-deterministic"))
    implementation(project(":serialization"))
    implementation(project(":serialization-deterministic"))
    implementation(project(":serialization-djvm"))
    implementation(project(":serialization-djvm:deserializers"))
    implementation(project(":node:djvm"))
    implementation(project(":node-api"))
    implementation(project(":tools:cliutils"))
    implementation(project(":tools:shell"))

    // Sandbox for deterministic contract verification
    implementation("net.corda.djvm:corda-djvm:${properties["djvmVersion"]}")

    testImplementation("com.google.jimfs:jimfs:${properties["jimfsVersion"]}")
    testImplementation("org.assertj:assertj-core:${properties["assertjVersion"]}")
    testImplementation("junit:junit:${properties["junitVersion"]}")

    testImplementation(project(":core-test-utils"))
    testImplementation(project(":finance:contracts"))
    testImplementation(project(":finance:workflows"))
    testImplementation(project(":test-utils"))
}
