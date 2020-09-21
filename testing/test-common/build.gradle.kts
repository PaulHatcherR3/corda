dependencies {
    implementation("org.slf4j:slf4j-api:${properties["slf4jVersion"]}")
    implementation("org.assertj:assertj-core:${properties["assertjVersion"]}")

    implementation(project(":core"))
}