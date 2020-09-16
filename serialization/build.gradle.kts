dependencies {
    implementation(project(":core"))

    implementation("com.google.guava:guava:${properties["guavaVersion"]}")
    implementation("org.ow2.asm:asm:${properties["asm_version"]}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${properties["kotlinVersion"]}")
    implementation("org.apache.qpid:proton-j:${properties["protonj_version"]}")
    implementation("org.slf4j:slf4j-api:${properties["slf4j_version"]}")
    implementation("org.apache.activemq:artemis-commons:${properties["artemis_version"]}")
    implementation("io.reactivex:rxjava:${properties["rxjava_version"]}")
    implementation("org.iq80.snappy:snappy:${properties["snappy_version"]}")

    testImplementation("junit:junit:${properties["junit_version"]}")
    testImplementation("org.jetbrains.kotlin:kotlin-test:${properties["kotlinVersion"]}")
    testImplementation("org.assertj:assertj-core:${properties["assertj_version"]}")
    testImplementation("com.nhaarman:mockito-kotlin:${properties["mockito_kotlin_version"]}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${properties["junit_jupiter_version"]}")
    testImplementation("org.hamcrest:hamcrest-library:${properties["hamcrest_version"]}")
    testImplementation("com.fasterxml.jackson.core:jackson-databind:${properties["jackson_version"]}")
}

val libGroupId = "net.corda"
val libArtifactId = "serialisation"
