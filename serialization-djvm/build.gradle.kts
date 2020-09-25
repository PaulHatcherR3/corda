dependencies {
    implementation(project(":core"))
    implementation(project(":serialization"))
    implementation(project(":serialization-djvm:deserializers"))

    api("net.corda.djvm:corda-djvm:${properties["djvmVersion"]}")
    implementation("org.apache.qpid:proton-j:${properties["protonjVersion"]}")
    implementation("com.google.guava:guava:${properties["guavaVersion"]}")

    testImplementation("org.junit.jupiter:junit-jupiter-api:${properties["junitJupiterVersion"]}")
    testImplementation("org.junit.jupiter:junit-jupiter-params:${properties["junitJupiterVersion"]}")
    testImplementation("org.assertj:assertj-core:${properties["assertjVersion"]}")
    testImplementation("org.bouncycastle:bcprov-jdk15on:${properties["bouncycastleVersion"]}")
    testImplementation("org.bouncycastle:bcpkix-jdk15on:${properties["bouncycastleVersion"]}")
}