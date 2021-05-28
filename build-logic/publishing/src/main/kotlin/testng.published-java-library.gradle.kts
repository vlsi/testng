plugins {
    id("testng.reproducible-builds")
    id("testng.java-library")
    id("testng.maven-publish")
}

java {
    withJavadocJar()
    withSourcesJar()
}

dependencies {
    // If the user adds core and api with different versions,
    // then Gradle would select **both** core and api with the same version
    implementation(platform(project(":bom")))
}

publishing {
    publications {
        create<MavenPublication>("custom") {
            from(components["java"])
//            groupId = project.group.toString()
//            artifactId = This.artifactId
//            version = project.version.toString()
//            suppressAllPomMetadataWarnings()
        }
    }
}
