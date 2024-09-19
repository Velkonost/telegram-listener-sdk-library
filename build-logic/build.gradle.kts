plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    api(libs.gradle.kotlin)
    api(libs.gradle.kotlinx.serialization)
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}
