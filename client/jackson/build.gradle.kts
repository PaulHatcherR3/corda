dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:${properties["kotlinVersion"]}")
    implementation("com.google.guava:guava:${properties["guavaVersion"]}")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${properties["jacksonVersion"]}")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:${properties["jacksonVersion"]}")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${properties["jacksonVersion"]}")
    implementation("org.bouncycastle:bcprov-jdk15on:${properties["bouncycastleVersion"]}")
    implementation("org.bouncycastle:bcpkix-jdk15on:${properties["bouncycastleVersion"]}")
    implementation("org.slf4j:slf4j-api:${properties["slf4jVersion"]}")

    implementation(project(":core"))
    implementation(project(":serialization"))

    testImplementation("org.assertj:assertj-core:${properties["assertjVersion"]}")
    testImplementation("junit:junit:${properties["junitVersion"]}")

    testImplementation("org.junit.vintage:junit-vintage-engine:${properties["junitVintageVersion"]}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${properties["junitJupiterVersion"]}")

    testImplementation(project(":finance:workflows"))
    testImplementation(project(":node-api"))
    testImplementation(project(":core-test-utils"))
    testImplementation(project(":test-common"))
    testImplementation(project(":test-utils"))
}

