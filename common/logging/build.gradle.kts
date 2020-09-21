dependencies {
    implementation("com.jcabi:jcabi-manifests:${properties["jcabiManifestsVersion"]}")
    implementation("org.apache.logging.log4j:log4j-core:${properties["log4jVersion"]}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${properties["kotlinVersion"]}")
    implementation(project(":common:validation"))

    testImplementation("junit:junit:${properties["junitVersion"]}")
    testImplementation("com.nhaarman:mockito-kotlin:${properties["mockitoKotlinVersion"]}")
    testImplementation("com.natpryce:hamkrest:${properties["hamkrestVersion"]}")
    testImplementation("org.jetbrains.kotlin:kotlin-test:${properties["kotlinVersion"]}")
}
