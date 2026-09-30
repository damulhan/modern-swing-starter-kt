plugins {
    kotlin("jvm") version "2.0.20"
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.example.swingapp"
version = "1.0.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

kotlin {
    jvmToolchain(17)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.formdev:flatlaf:3.5.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.8.1")

    testImplementation(kotlin("test"))
}

application {
    mainClass.set("com.example.swingapp.ApplicationKt")
}

tasks.named<JavaExec>("run") {
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.example.swingapp.ApplicationKt")
}

tasks.shadowJar {
    archiveBaseName.set("modern-swing-starter-kt")
    archiveClassifier.set("")
    archiveVersion.set("1.0.0")
    mergeServiceFiles()
}

tasks.test {
    useJUnitPlatform()
}
