dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${properties["kotlinVersion"]}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${properties["kotlinVersion"]}")
    implementation("co.paralleluniverse:quasar-core:${properties["quasarVersion11"]}")

    implementation(project(":core"))
    implementation(project(":core-test-utils"))
    testImplementation(project(":finance:contracts"))
    testImplementation(project(":finance:workflows"))

    testImplementation("junit:junit:${properties["junit_version"]}")
}