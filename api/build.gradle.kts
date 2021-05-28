plugins {
    id("testng.published-java-platform")
}

// This is a convenience artifact to add dependencies to all the API jars
javaPlatform.allowDependencies()

dependencies {
    api(projects.asserts)
    api(projects.coreApi)
}
