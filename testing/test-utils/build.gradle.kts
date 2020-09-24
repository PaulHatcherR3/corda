dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${properties["kotlinVersion"]}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${properties["kotlinVersion"]}")
    implementation("com.google.guava:guava:${properties["guavaVersion"]}")
    implementation("com.nhaarman:mockito-kotlin:${properties["mockitoKotlinVersion"]}")
    implementation("com.github.ben-manes.caffeine:caffeine:${properties["caffeineVersion"]}")
    implementation("io.dropwizard.metrics:metrics-jmx:${properties["metricsVersion"]}")
    implementation("com.squareup.okhttp3:okhttp:${properties["okhttpVersion"]}")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${properties["jacksonVersion"]}")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:${properties["jacksonVersion"]}")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${properties["jacksonVersion"]}")
    implementation("org.hibernate:hibernate-core:${properties["hibernateVersion"]}")
    implementation("javax.persistence:javax.persistence-api:${properties["persistenceApiVersion"]}")
    implementation("co.paralleluniverse:quasar-core:${properties["quasarVersion"]}")
    implementation("org.bouncycastle:bcprov-jdk15on:${properties["bouncycastleVersion"]}")
    implementation("org.bouncycastle:bcpkix-jdk15on:${properties["bouncycastleVersion"]}")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:${properties["log4jVersion"]}")
    implementation("org.apache.logging.log4j:log4j-web:${properties["log4jVersion"]}")
    implementation("com.typesafe:config:${properties["typesafeConfigVersion"]}")

    implementation(project(":client:jackson"))
    implementation(project(":core"))
    implementation(project(":core-test-utils"))
    implementation(project(":node"))
    implementation(project(":node-api"))
    implementation(project(":test-common"))
    implementation(project(":serialization"))

    testImplementation("org.apache.commons:commons-lang3:${properties["commonsLangVersion"]}")
    testImplementation("org.jetbrains.kotlin:kotlin-test:${properties["kotlinVersion"]}")
    testImplementation("org.assertj:assertj-core:${properties["assertjVersion"]}")
    testImplementation("junit:junit:${properties["junitVersion"]}")
}