import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version "1.7.10"
}

val kotlinVersion: String by project
val lwjglVersion: String by project

group = "io.github.gaming32"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        name = "gaming32-releases"
        url = uri("https://maven.jemnetworks.com/releases")
    }
}

dependencies {
    implementation("io.github.holygrailsortproject:rewritten-grailsort-jvm:1.0")

    implementation("org.jetbrains.kotlin:kotlin-scripting-common:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm-host:$kotlinVersion")

    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))
    implementation("org.lwjgl", "lwjgl")
    implementation("org.lwjgl", "lwjgl-glfw")
    implementation("org.lwjgl", "lwjgl-opengl")
    for (lwjglNatives in arrayOf(
        "natives-linux-arm64", "natives-linux",
        "natives-macos-arm64", "natives-macos",
        "natives-windows-arm64", "natives-windows", "natives-windows-x86"
    )) {
        runtimeOnly("org.lwjgl", "lwjgl", classifier = lwjglNatives)
        runtimeOnly("org.lwjgl", "lwjgl-glfw", classifier = lwjglNatives)
        runtimeOnly("org.lwjgl", "lwjgl-opengl", classifier = lwjglNatives)
    }
}

application {
    mainClass.set("io.github.gaming32.sortviskt.MainWindowKt")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
