dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${properties["kotlinVersion"]}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${properties["kotlinVersion"]}")

    implementation("co.paralleluniverse:quasar-core:${properties["quasarVersion"]}")
    implementation("javax.persistence:javax.persistence-api:${properties["persistenceApiVersion"]}")
    implementation("org.hibernate:hibernate-core:${properties["hibernateVersion"]}")
    implementation("org.slf4j:slf4j-api:${properties["slf4jVersion"]}")

    implementation(project(":confidential-identities"))
    implementation(project(":core"))
    implementation(project(":finance:contracts"))

    testImplementation("junit:junit:${properties["junitVersion"]}")
    testImplementation("org.apache.qpid:proton-j:${properties["protonjVersion"]}")

    testImplementation(project(":core"))
    testImplementation(project(":serialization"))
    testImplementation(project(":core-test-utils"))
    testImplementation(project(":test-utils"))
}