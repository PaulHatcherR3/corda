dependencies {
    implementation(project(":core"))
    implementation(project(":serialization"))

    implementation("org.apache.qpid:proton-j:${properties["protonjVersion"]}")
    implementation("com.google.guava:guava:${properties["guavaVersion"]}")
}