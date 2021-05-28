plugins {
    id("testng.published-java-library")
}

dependencies {
    api(project(":core"))
    api("org.apache.ant:ant:_")

    testImplementation("org.apache.ant:ant-testutil:_")
    testImplementation("org.assertj:assertj-core:_")
}
