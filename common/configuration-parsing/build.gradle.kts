dependencies {
    implementation(project(":common:validation"))
    implementation("com.typesafe:config:${properties["typesafeConfigVersion"]}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${properties["kotlinVersion"]}")

    testImplementation("junit:junit:${properties["junit_version"]}")
    testImplementation("org.jetbrains.kotlin:kotlin-test:${properties["kotlinVersion"]}")
    testImplementation("org.assertj:assertj-core:${properties["assertj_version"]}")
}