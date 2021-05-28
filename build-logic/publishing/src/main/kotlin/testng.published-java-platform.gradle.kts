plugins {
    id("testng.reproducible-builds")
    id("testng.java-platform")
    id("testng.maven-publish")
}

publishing {
    publications {
        create<MavenPublication>("custom") {
            from(components["javaPlatform"])
        }
    }
}
