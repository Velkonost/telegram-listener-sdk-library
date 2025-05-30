import com.vanniktech.maven.publish.JavaLibrary
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id("default-convention")
    id("serialization-convention")
    alias(libs.plugins.kmm.publish)
    alias(libs.plugins.dokka)
}

group = "com.velkonost"
version = libs.versions.telegram.listener.sdk.get()
description = "Simple TDLib implementation in Kotlin for listen messages"

mavenPublishing {
    configure(
        KotlinJvm(
            sourcesJar = true,
            javadocJar = JavadocJar.Dokka("dokkaHtml"),
        )
    )
    coordinates(
        groupId = project.group.toString(),
        artifactId = "telegram-listener-sdk",
        version = libs.versions.telegram.listener.sdk.get()
    )

    pom {
        name.set("Telegram Listener SDK")
        description.set(project.description)
        inceptionYear.set("2025")
        url.set("https://github.com/Velkonost/telegram-listener-sdk")

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

        scm {
            url.set("https://github.com/Velkonost/telegram-listener-sdk")
        }
    }

    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()
}

//publishing {
//
//    publications {
//        create<MavenPublication>("release") {
//            from(components["java"])
//
//            groupId = "com.velkonost"
//            artifactId = "telegram-listener-sdk"
//
//            pom {
//                name.set(project.properties["POM_NAME"].toString())
//                description.set(project.description)
//                url.set("https://github.com/Velkonost/telegram-listener-sdk")
//                issueManagement {
//                    url.set("https://github.com/Velkonost/telegram-listener-sdk/issues")
//                }
//
//                scm {
//                    url.set("https://github.com/Velkonost/telegram-listener-sdk")
//                    connection.set("scm:git://github.com/Velkonost/telegram-listener-sdk.git")
//                    developerConnection.set("scm:git://github.com/Velkonost/telegram-listener-sdk.git")
//                }
//
//                licenses {
//                    license {
//                        name.set("The Apache Software License, Version 2.0")
//                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
//                        distribution.set("repo")
//                    }
//                }
//
//                developers {
//                    developer {
//                        id.set("velkonost")
//                        name.set("Artem Klimenko")
//                        email.set("velkonost@gmail.com")
//                        url.set("t.me/velkonost")
//                    }
//                }
//            }
//
//        }
//    }
//
//    repositories {
//        maven {
//            setUrl(layout.buildDirectory.dir("staging-deploy"))
//        }
//    }
//}

//jreleaser {
//    project {
//        inceptionYear = "2024"
//        author("@velkonost")
//    }
//    gitRootSearch = true
//    release {
//        github {
//            skipRelease = true
//            skipTag = true
//            sign = true
//            branch = "main"
//            branchPush = "main"
//            overwrite = true
//        }
//    }
//    signing {
//        active = Active.ALWAYS
//        armored = true
//        verify = true
//    }
//    deploy {
//        maven {
//            mavenCentral.create("sonatype") {
//                active = Active.ALWAYS
//                url = "https://central.sonatype.com/api/v1/publisher"
//                stagingRepository(layout.buildDirectory.dir("staging-deploy").get().toString())
//                setAuthorization("Basic")
//                retryDelay = 60
//            }
//        }
//    }
//}