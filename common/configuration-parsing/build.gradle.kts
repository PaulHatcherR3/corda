dependencies {
    implementation(project(":common:validation"))
    implementation("com.typesafe:config:${properties["typesafeConfigVersion"]}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${properties["kotlinVersion"]}")

    testImplementation("junit:junit:${properties["junitVersion"]}")
    testImplementation("org.jetbrains.kotlin:kotlin-test:${properties["kotlinVersion"]}")
    testImplementation("org.assertj:assertj-core:${properties["assertjVersion"]}")
}