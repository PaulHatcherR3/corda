dependencies {
    implementation(project(":core"))
    implementation(project(":common:logging"))

    implementation("org.apache.commons:commons-lang3:${properties["commonsLangVersion"]}")
    implementation("commons-io:commons-io:${properties["commonsIoVersion"]}")
    implementation("info.picocli:picocli:${properties["picocliVersion"]}")
    implementation("org.slf4j:slf4j-api:${properties["slf4jVersion"]}")
    implementation("org.fusesource.jansi:jansi:${properties["jansiVersion"]}")
}
