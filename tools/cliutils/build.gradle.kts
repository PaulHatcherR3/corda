dependencies {
    implementation(project(":core"))
    implementation(project(":common:logging"))

    implementation("org.apache.commons:commons-lang3:${properties["commons_lang_version"]}")
    implementation("commons-io:commons-io:${properties["commons_io_version"]}")
    implementation("info.picocli:picocli:${properties["picocli_version"]}")
    implementation("org.slf4j:slf4j-api:${properties["slf4j_version"]}")
    implementation("org.fusesource.jansi:jansi:${properties["jansi_version"]}")
}
