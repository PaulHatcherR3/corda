dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${properties["kotlinVersion"]}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${properties["kotlinVersion"]}")
    implementation("com.google.guava:guava:${properties["guavaVersion"]}")
    implementation("io.reactivex:rxjava:${properties["rxjava_version"]}")
    implementation("com.github.ben-manes.caffeine:caffeine:${properties["caffeineVersion"]}")
    implementation("org.apache.activemq:artemis-core-client:${properties["artemis_version"]}")
    implementation("org.apache.activemq:artemis-server:${properties["artemis_version"]}")
    implementation("org.apache.activemq:artemis-amqp-protocol:${properties["artemis_version"]}")
    implementation("org.slf4j:slf4j-api:${properties["slf4j_version"]}")

    testImplementation(project(":core-test-utils"))

    implementation(project(":core"))
    implementation(project(":node-api"))
    implementation(project(":serialization"))
}