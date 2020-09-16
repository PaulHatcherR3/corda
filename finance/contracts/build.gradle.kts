dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:${properties["kotlinVersion"]}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${properties["kotlinVersion"]}")
    implementation("org.hibernate:hibernate-core:${properties["hibernate_version"]}")
    implementation("org.slf4j:slf4j-api:${properties["slf4j_version"]}")
    implementation("javax.persistence:javax.persistence-api:${properties["persistence-api_version"]}")

    implementation(project(":core"))

    testImplementation(project(":finance:workflows"))

    testImplementation("org.jetbrains.kotlin:kotlin-test:${properties["kotlinVersion"]}")
    testImplementation("com.nhaarman:mockito-kotlin:${properties["mockito_kotlin_version"]}")
    testImplementation("junit:junit:${properties["junit_version"]}")
    testImplementation(project(":core-test-utils"))
    testImplementation(project(":test-utils"))
}