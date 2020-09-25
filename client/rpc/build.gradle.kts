dependencies {
    implementation("com.google.guava:guava:${properties["guavaVersion"]}")
    implementation("io.reactivex:rxjava:${properties["rxjavaVersion"]}")
    implementation("com.github.ben-manes.caffeine:caffeine:${properties["caffeineVersion"]}")
    implementation("org.apache.activemq:artemis-core-client:${properties["artemisVersion"]}")
    implementation("org.apache.activemq:artemis-server:${properties["artemisVersion"]}")
    implementation("org.apache.activemq:artemis-amqp-protocol:${properties["artemisVersion"]}")
    implementation("org.slf4j:slf4j-api:${properties["slf4jVersion"]}")

    testImplementation(project(":core-test-utils"))

    implementation(project(":core"))
    implementation(project(":node-api"))
    implementation(project(":serialization"))
}