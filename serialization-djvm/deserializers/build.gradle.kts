dependencies {
    implementation(project(":core"))
    implementation(project(":serialization"))

    implementation("org.apache.qpid:proton-j:${properties["protonj_version"]}")
}
