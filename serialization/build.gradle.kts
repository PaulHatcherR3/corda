dependencies {
    implementation(project(":core"))

    implementation("com.google.guava:guava:${properties["guavaVersion"]}")
    implementation("org.ow2.asm:asm:${properties["asmVersion"]}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${properties["kotlinVersion"]}")
    implementation("org.apache.qpid:proton-j:${properties["protonjVersion"]}")
    implementation("org.slf4j:slf4j-api:${properties["slf4jVersion"]}")
    implementation("org.apache.activemq:artemis-commons:${properties["artemisVersion"]}")
    implementation("io.reactivex:rxjava:${properties["rxjavaVersion"]}")
    implementation("org.iq80.snappy:snappy:${properties["snappyVersion"]}")

    testImplementation("junit:junit:${properties["junitVersion"]}")
    testImplementation("org.jetbrains.kotlin:kotlin-test:${properties["kotlinVersion"]}")
    testImplementation("org.assertj:assertj-core:${properties["assertjVersion"]}")
    testImplementation("com.nhaarman:mockito-kotlin:${properties["mockitoKotlinVersion"]}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${properties["junitJupiterVersion"]}")
    testImplementation("org.hamcrest:hamcrest-library:${properties["hamcrestVersion"]}")
    testImplementation("com.fasterxml.jackson.core:jackson-databind:${properties["jacksonVersion"]}")
}

val libArtifactId = "serialisation"
