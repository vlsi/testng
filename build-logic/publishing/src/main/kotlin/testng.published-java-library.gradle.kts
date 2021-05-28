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
    implementation(platform(project(":testng-bom")))
    // For some reason this can't be in code-quality/testng.testing :(
    testImplementation(project(":testng-test-kit"))
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
