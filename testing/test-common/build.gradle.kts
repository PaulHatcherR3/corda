dependencies {
    implementation("org.slf4j:slf4j-api:${properties["slf4j_version"]}")
    implementation("org.assertj:assertj-core:${properties["assertj_version"]}")

    implementation(project(":core"))
}