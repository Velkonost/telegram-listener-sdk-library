plugins {
    id("default-convention")
    id("serialization-convention")
    id("maven-publish")
    alias(libs.plugins.jreleaser)
}

java {
    withJavadocJar()
    withSourcesJar()
}

group = "io.github.velkonost"
version = libs.versions.telegram.listener.sdk.get()
description = "Simple TDLib implementation in Kotlin for listen messages"

publishing {

    publications {
        create<MavenPublication>("release") {
            from(components["java"])

            groupId = "io.github.velkonost"
            artifactId = "telegram-listener-sdk"

            pom {
                name.set(project.properties["POM_NAME"].toString())
                description.set(project.description)
                url.set("https://github.com/Velkonost/telegram-listener-sdk")
                issueManagement {
                    url.set("https://github.com/Velkonost/telegram-listener-sdk/issues")
                }

                scm {
                    url.set("https://github.com/Velkonost/telegram-listener-sdk")
                    connection.set("scm:git://github.com/Velkonost/telegram-listener-sdk.git")
                    developerConnection.set("scm:git://github.com/Velkonost/telegram-listener-sdk.git")
                }

                licenses {
                    license {
                        name.set("The Apache Software License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        distribution.set("repo")
                    }
                }

                developers {
                    developer {
                        id.set("velkonost")
                        name.set("Artem Klimenko")
                        email.set("velkonost@gmail.com")
                        url.set("t.me/velkonost")
                    }
                }
            }

        }
    }

    repositories {
        maven {
            setUrl(layout.buildDirectory.dir("staging-deploy"))
        }
    }
}