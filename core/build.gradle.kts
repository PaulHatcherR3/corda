dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:${properties["kotlinVersion"]}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${properties["kotlinVersion"]}")
    implementation("com.google.guava:guava:${properties["guavaVersion"]}")
    implementation("com.google.code.findbugs:jsr305:${properties["jsr305Version"]}")
    implementation("io.reactivex:rxjava:${properties["rxjavaVersion"]}")
    implementation("org.slf4j:slf4j-api:${properties["slf4jVersion"]}")
    implementation("co.paralleluniverse:quasar-core:${properties["quasarVersion"]}")
    implementation("com.github.ben-manes.caffeine:caffeine:${properties["caffeineVersion"]}")
    implementation("org.hibernate:hibernate-core:${properties["hibernateVersion"]}")
    implementation("javax.persistence:javax.persistence-api:${properties["persistenceApiVersion"]}")
    implementation("org.bouncycastle:bcprov-jdk15on:${properties["bouncycastleVersion"]}")
    implementation("org.bouncycastle:bcpkix-jdk15on:${properties["bouncycastleVersion"]}")
    implementation("net.i2p.crypto:eddsa:${properties["eddsaVersion"]}")
    implementation("io.netty:netty-common:${properties["nettyVersion"]}")
    implementation("org.apache.commons:commons-lang3:${properties["commonsLangVersion"]}")
    implementation("io.github.classgraph:classgraph:${properties["classgraphVersion"]}")

    testImplementation("junit:junit:${properties["junitVersion"]}")
    testImplementation("org.assertj:assertj-core:${properties["assertjVersion"]}")
    testImplementation("org.ow2.asm:asm:${properties["asmVersion"]}")
}

// Added to facilitate core-tests
val testConfig = configurations.create("testArtifacts") {
    extendsFrom(configurations["testCompile"])
}

// Added to facilitate core-tests
tasks.register<Jar>("testJar") {
    dependsOn("testClasses")
    archiveBaseName.set("${project.name}-test")
    from(sourceSets["test"].output.classesDirs)
}

// Added to facilitate core-tests
// Exposes test classes for other modules
artifacts {
    add("testArtifacts", tasks.named<Jar>("testJar") )
}

val libArtifactId = "core"