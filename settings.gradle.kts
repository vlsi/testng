pluginManagement {
    includeBuild("build-logic")
}

rootProject.name = "testng-root"

plugins {
    id("com.gradle.enterprise") version "3.6.1"
    id("de.fayard.refreshVersions") version "0.10.0"
}

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
        publishAlways()
    }
}

include(":testng")
include(":testng-bom")
include(":testng-api")
include(":testng-asserts")
include(":testng-ant")
include(":testng-collections")
include(":testng-core-api")
include(":testng-core")
include(":testng-test-kit")

enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
