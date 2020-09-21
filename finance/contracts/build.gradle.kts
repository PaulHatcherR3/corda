dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:${properties["kotlinVersion"]}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${properties["kotlinVersion"]}")
    implementation("org.hibernate:hibernate-core:${properties["hibernateVersion"]}")
    implementation("org.slf4j:slf4j-api:${properties["slf4jVersion"]}")
    implementation("javax.persistence:javax.persistence-api:${properties["persistence-apiVersion"]}")

    implementation(project(":core"))

    testImplementation(project(":finance:workflows"))

    testImplementation("org.jetbrains.kotlin:kotlin-test:${properties["kotlinVersion"]}")
    testImplementation("com.nhaarman:mockito-kotlin:${properties["mockitoKotlinVersion"]}")
    testImplementation("junit:junit:${properties["junitVersion"]}")
    testImplementation(project(":core-test-utils"))
    testImplementation(project(":test-utils"))
}