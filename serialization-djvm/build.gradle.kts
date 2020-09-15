dependencies {
    implementation(project(":core"))
    implementation(project(":serialization"))
    implementation(project(":serialization-djvm:deserializers"))

    api("net.corda.djvm:corda-djvm:${properties["djvmVersion"]}")
    implementation("org.apache.qpid:proton-j:${properties["protonj_version"]}")
    implementation("com.google.guava:guava:${properties["guavaVersion"]}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${properties["kotlinVersion"]}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${properties["kotlinVersion"]}")

    testImplementation("org.junit.jupiter:junit-jupiter-api:${properties["junit_jupiter_version"]}")
    testImplementation("org.junit.jupiter:junit-jupiter-params:${properties["junit_jupiter_version"]}")
    testImplementation("org.assertj:assertj-core:${properties["assertj_version"]}")
    testImplementation("org.bouncycastle:bcprov-jdk15on:${properties["bouncycastleVersion"]}")
    testImplementation("org.bouncycastle:bcpkix-jdk15on:${properties["bouncycastleVersion"]}")
}