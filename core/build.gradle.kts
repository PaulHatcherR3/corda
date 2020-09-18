dependencies {
    implementation("com.google.code.findbugs:jsr305:${properties["jsr305Version"]}")
    implementation("io.reactivex:rxjava:${properties["rxjava_version"]}")
    implementation("org.slf4j:slf4j-api:${properties["slf4j_version"]}")
    implementation("co.paralleluniverse:quasar-core:${properties["quasarVersion11"]}")
    implementation("com.github.ben-manes.caffeine:caffeine:${properties["caffeineVersion"]}")
    implementation("org.hibernate:hibernate-core:${properties["hibernate_version"]}")
    implementation("javax.persistence:javax.persistence-api:${properties["persistence-api_version"]}")
    implementation("org.bouncycastle:bcprov-jdk15on:${properties["bouncycastleVersion"]}")
    implementation("org.bouncycastle:bcpkix-jdk15on:${properties["bouncycastleVersion"]}")
    implementation("net.i2p.crypto:eddsa:${properties["eddsa_version"]}")
    implementation("io.netty:netty-common:${properties["netty_version"]}")
    implementation("org.apache.commons:commons-lang3:${properties["commons_lang_version"]}")
    implementation("io.github.classgraph:classgraph:${properties["classgraphVersion"]}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${properties["kotlinVersion"]}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${properties["kotlinVersion"]}")

    testImplementation("junit:junit:${properties["junit_version"]}")
    testImplementation("org.jetbrains.kotlin:kotlin-test:${properties["kotlinVersion"]}")
    testImplementation("org.assertj:assertj-core:${properties["assertj_version"]}")
    testImplementation("com.nhaarman:mockito-kotlin:${properties["mockito_kotlin_version"]}")
}

val libArtifactId = "core"