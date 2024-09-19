import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.repositories
import util.libs

plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.jupiter.api)
    testRuntimeOnly(libs.jupiter.engine)
}

tasks.test {
    useJUnitPlatform()
}