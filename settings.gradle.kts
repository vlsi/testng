pluginManagement {
    includeBuild("build-logic")
}

rootProject.name = "testng"

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

include(":bom")
include(":api")
include(":asserts")
include(":ant")
include(":collections")
include(":core-api")
include(":core")
include(":test-kit")

enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
