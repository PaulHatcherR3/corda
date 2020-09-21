dependencies {
    implementation(project(":core"))

    // SLF4J: commons-logging bindings for a SLF4J back end
    implementation("org.slf4j:slf4j-api:${properties["slf4jVersion"]}")
}