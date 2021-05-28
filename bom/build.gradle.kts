plugins {
    id("testng.published-java-platform")
}

// Add a convenience pom.xml that sets all the versions
dependencies {
    constraints {
        api(projects.ant)
        api(projects.api)
        api(projects.asserts)
        api(projects.collections)
        api(projects.coreApi)
        api(projects.core)
    }
}
