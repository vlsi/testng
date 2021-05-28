plugins {
    id("testng.published-java-library")
}

dependencies {
    implementation(projects.collections) {
        because("Lists.newArrayList")
    }

    testImplementation("org.testng:testng:7.3.0") {
        because("core depends on assertions and we need testng to test assertions")
    }
}
