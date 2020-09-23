dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${properties["kotlinVersion"]}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${properties["kotlinVersion"]}")
    implementation("co.paralleluniverse:quasar-core:${properties["quasarVersion"]}")
    implementation("org.slf4j:slf4j-api:${properties["slf4jVersion"]}")

    implementation(project(":core"))
    implementation(project(":core-test-utils"))
    testImplementation(project(":finance:contracts"))
    testImplementation(project(":finance:workflows"))

    testImplementation("junit:junit:${properties["junit_version"]}")
}