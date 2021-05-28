plugins {
    id("testng.published-java-library")
}

dependencies {
    api(project(":core"))
    api("org.apache.ant:ant:_")

    testImplementation(projects.asserts)
    testImplementation("org.apache.ant:ant-testutil:_")
}
