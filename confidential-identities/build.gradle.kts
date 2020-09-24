dependencies {
    implementation("co.paralleluniverse:quasar-core:${properties["quasarVersion"]}")
    implementation("org.slf4j:slf4j-api:${properties["slf4jVersion"]}")

    implementation(project(":core"))
    implementation(project(":core-test-utils"))
    testImplementation(project(":finance:contracts"))
    testImplementation(project(":finance:workflows"))

    testImplementation("junit:junit:${properties["junit_version"]}")
}