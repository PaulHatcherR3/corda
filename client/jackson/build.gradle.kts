dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${properties["kotlinVersion"]}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${properties["kotlinVersion"]}")
    implementation("com.google.guava:guava:${properties["guavaVersion"]}")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${properties["jackson_version"]}")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:${properties["jackson_version"]}")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${properties["jackson_version"]}")
    implementation("org.bouncycastle:bcprov-jdk15on:${properties["bouncycastleVersion"]}")
    implementation("org.bouncycastle:bcpkix-jdk15on:${properties["bouncycastleVersion"]}")
    implementation("org.slf4j:slf4j-api:${properties["slf4j_version"]}")

    implementation(project(":core"))
    implementation(project(":serialization"))
}