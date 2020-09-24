dependencies {
    implementation("org.hibernate:hibernate-core:${properties["hibernateVersion"]}")
    implementation("org.slf4j:slf4j-api:${properties["slf4jVersion"]}")
    implementation("javax.persistence:javax.persistence-api:${properties["persistenceApiVersion"]}")

    implementation(project(":core"))

    testImplementation(project(":finance:workflows"))

    testImplementation("junit:junit:${properties["junitVersion"]}")
    testImplementation(project(":core-test-utils"))
    testImplementation(project(":test-utils"))
}