dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${properties["kotlinVersion"]}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${properties["kotlinVersion"]}")
    implementation("com.google.guava:guava:${properties["guavaVersion"]}")
    implementation("com.nhaarman:mockito-kotlin:${properties["mockito_kotlin_version"]}")
    implementation("com.github.ben-manes.caffeine:caffeine:${properties["caffeine_version"]}")
    implementation("io.dropwizard.metrics:metrics-jmx:${properties["metrics_version"]}")
    implementation("com.squareup.okhttp3:okhttp:${properties["okhttp_version"]}")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${properties["jackson_version"]}")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:${properties["jackson_version"]}")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${properties["jackson_version"]}")
    implementation("org.hibernate:hibernate-core:${properties["hibernate_version"]}")
    implementation("javax.persistence:javax.persistence-api:${properties["persistence-api_version"]}")
    implementation("co.paralleluniverse:quasar-core:${properties["quasarVersion11"]}")
    implementation("org.bouncycastle:bcprov-jdk15on:${properties["bouncycastleVersion"]}")
    implementation("org.bouncycastle:bcpkix-jdk15on:${properties["bouncycastleVersion"]}")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:${properties["log4j_version"]}")
    implementation("org.apache.logging.log4j:log4j-web:${properties["log4j_version"]}")
    implementation("com.typesafe:config:${properties["typesafe_config_version"]}")

    implementation(project(":client:jackson"))
    implementation(project(":core"))
    implementation(project(":core-test-utils"))
    implementation(project(":node"))
    implementation(project(":node-api"))
    implementation(project(":test-common"))
    implementation(project(":serialization"))

    testImplementation("org.apache.commons:commons-lang3:${properties["commons_lang_version"]}")
    testImplementation("org.jetbrains.kotlin:kotlin-test:${properties["kotlinVersion"]}")
    testImplementation("org.assertj:assertj-core:${properties["assertj_version"]}")
    testImplementation("junit:junit:${properties["junit_version"]}")
}