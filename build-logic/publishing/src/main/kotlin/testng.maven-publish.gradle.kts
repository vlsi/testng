plugins {
    `maven-publish`
    id("testng.local-maven-repo")
    id("testng.signing")
}

val scm = "github.com/cbeust/testng"

publishing {
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
                    url.set("https://$scm/issues")
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
                    connection.set("scm:git:git://$scm.git")
                    url.set("https://$scm")
                }
            }
        }
    }
}
