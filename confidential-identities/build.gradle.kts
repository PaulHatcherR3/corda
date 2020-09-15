dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${properties["kotlinVersion"]}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${properties["kotlinVersion"]}")
//    implementation("com.google.guava:guava:${properties["guavaVersion"]}")
    implementation("co.paralleluniverse:quasar-core:${properties["quasarVersion11"]}")

    implementation(project(":core"))

}