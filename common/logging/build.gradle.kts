dependencies {
    implementation("com.jcabi:jcabi-manifests:${properties["jcabi_manifests_version"]}")
    implementation("org.apache.logging.log4j:log4j-core:${properties["log4j_version"]}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${properties["kotlinVersion"]}")
    implementation(project(":common:validation"))

    testImplementation("junit:junit:${properties["junit_version"]}")
    testImplementation("com.nhaarman:mockito-kotlin:${properties["mockito_kotlin_version"]}")
    testImplementation("com.natpryce:hamkrest:${properties["hamkrest_version"]}")
    testImplementation("org.jetbrains.kotlin:kotlin-test:${properties["kotlinVersion"]}")
}
