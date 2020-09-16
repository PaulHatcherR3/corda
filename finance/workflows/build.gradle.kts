dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:${properties["kotlinVersion"]}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${properties["kotlinVersion"]}")
    implementation("co.paralleluniverse:quasar-core:${properties["quasarVersion11"]}")
    implementation("javax.persistence:javax.persistence-api:${properties["persistence-api_version"]}")
    implementation("org.hibernate:hibernate-core:${properties["hibernate_version"]}")
    implementation("org.slf4j:slf4j-api:${properties["slf4j_version"]}")

    implementation(project(":confidential-identities"))
    implementation(project(":core"))
    implementation(project(":finance:contracts"))

    testImplementation("org.jetbrains.kotlin:kotlin-test:${properties["kotlinVersion"]}")
    testImplementation("junit:junit:${properties["junit_version"]}")
    testImplementation("org.apache.qpid:proton-j:${properties["protonj_version"]}")

    testImplementation(project(":core"))
    testImplementation(project(":serialization"))
    testImplementation(project(":core-test-utils"))
    testImplementation(project(":test-utils"))
}