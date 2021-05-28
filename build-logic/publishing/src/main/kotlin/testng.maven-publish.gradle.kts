plugins {
    `maven-publish`
    id("testng.signing")
}

object This {
    const val version = "7.5.0-SNAPSHOT"
    const val artifactId = "testng"
    const val groupId = "org.testng"
    const val description = "Testing framework for Java"
    const val url = "https://testng.org"
    const val scm = "github.com/cbeust/testng"

    // Should not need to change anything below
    const val name = "TestNG"
    const val vendor = name
}

publishing {
    repositories {
        maven {
            name = "myRepo"
            url = uri("file://$buildDir/repo")
        }
    }
    publications {
        withType<MavenPublication>().configureEach {
            pom {
                name.set(artifactId)
                description.set(providers.gradleProperty("description"))
                val projectUrl = rootProject.providers.gradleProperty("project.url")
                url.set(projectUrl)
                licenses {
                    license {
                        name.set("Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                issueManagement {
                    system.set("Github")
                    url.set("https://${This.scm}/issues")
                }
                developers {
                    developer {
                        id.set("cbeust")
                        name.set("Cedric Beust")
                        email.set("cedric@beust.com")
                    }
                    developer {
                        id.set("juherr")
                        name.set("Julien Herr")
                        email.set("julien@herr.fr")
                    }
                    developer {
                        id.set("krmahadevan")
                        name.set("Krishnan Mahadevan")
                        email.set("krishnan.mahadevan1978@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://${This.scm}.git")
                    url.set("https://${This.scm}")
                }
            }
        }
    }
}
