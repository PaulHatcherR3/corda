dependencies {
    implementation("com.google.guava:guava:${properties["guavaVersion"]}")
    implementation("com.google.code.findbugs:jsr305:${properties["jsr305Version"]}")
    implementation("io.reactivex:rxjava:${properties["rxjavaVersion"]}")
    implementation("org.slf4j:slf4j-api:${properties["slf4jVersion"]}")
    implementation("co.paralleluniverse:quasar-core:${properties["quasarVersion11"]}")
    implementation("com.github.ben-manes.caffeine:caffeine:${properties["caffeineVersion"]}")
    implementation("org.hibernate:hibernate-core:${properties["hibernateVersion"]}")
    implementation("javax.persistence:javax.persistence-api:${properties["persistence-apiVersion"]}")
    implementation("org.bouncycastle:bcprov-jdk15on:${properties["bouncycastleVersion"]}")
    implementation("org.bouncycastle:bcpkix-jdk15on:${properties["bouncycastleVersion"]}")
    implementation("net.i2p.crypto:eddsa:${properties["eddsaVersion"]}")
    implementation("io.netty:netty-common:${properties["nettyVersion"]}")
    implementation("org.apache.commons:commons-lang3:${properties["commonsLangVersion"]}")
    implementation("io.github.classgraph:classgraph:${properties["classgraphVersion"]}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${properties["kotlinVersion"]}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${properties["kotlinVersion"]}")

    testImplementation("junit:junit:${properties["junitVersion"]}")
    testImplementation("org.jetbrains.kotlin:kotlin-test:${properties["kotlinVersion"]}")
    testImplementation("org.assertj:assertj-core:${properties["assertjVersion"]}")
    testImplementation("com.nhaarman:mockito-kotlin:${properties["mockitoKotlinVersion"]}")
    testImplementation("org.ow2.asm:asm:${properties["asmVersion"]}")
}

val libArtifactId = "core"