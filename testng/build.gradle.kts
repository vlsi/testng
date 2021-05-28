import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("testng.published-java-library")
    id("com.github.johnrengelman.shadow") apply false
}

description = "Jar with all TestNG classes"

//<editor-fold desc="Prepare configurations for testng-all jars inference">
val shadedDependencyElements by configurations.creating {
    description = "Declares which modules to aggregate into testng-all"
    isCanBeConsumed = false
    isCanBeResolved = false
    isVisible = false
}

val testngJars by configurations.creating {
    extendsFrom(shadedDependencyElements)
    resolutionStrategy.dependencySubstitution.all {
        // prune third-party dependencies, so testng-all.jar contains only testng classes
        if (requested !is ProjectComponentSelector) {
            // It is not clear how to remove dependency, however,
            // replacing everything with collections seems to be harmless
            useTarget(project(":testng-collections"))
        }
    }
    attributes {
        attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.LIBRARY))
        attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_API))
    }
}
//</editor-fold>

java {
    registerFeature("ant") {
        usingSourceSet(sourceSets["main"])
    }

    registerFeature("guice") {
        usingSourceSet(sourceSets["main"])
    }

    registerFeature("junit") {
        usingSourceSet(sourceSets["main"])
    }

    registerFeature("yaml") {
        usingSourceSet(sourceSets["main"])
    }
}

dependencies {
    // Note: it is enough to mention key projects here, and testng transitives
    // would be selected automatically
    shadedDependencyElements(platform(projects.testngApi))
    shadedDependencyElements(projects.testngAnt)
    shadedDependencyElements(projects.testngCore)

    // Note: it is important to list all third-party dependencies manually
    // Automatic inference by Gradle does not seem to be trivial :(
    api("com.google.code.findbugs:jsr305:_")
    api("com.beust:jcommander:_")
    "antApi"("org.apache.ant:ant:_")
    "guiceApi"(platform("com.google.inject:guice-bom:_"))
    "guiceApi"("com.google.inject:guice::no_aop")
    "junitApi"("junit:junit:_")
    "yamlApi"("org.yaml:snakeyaml:_")

    implementation("org.webjars:jquery:_")
}

val testngAllJar by tasks.registering(ShadowJar::class) {
    configurations = listOf(testngJars)
    archiveClassifier.set("all")
}

//<editor-fold desc="Use testng-all.jar for publication instead of testng.jar">
configurations.all {
    if (name.endsWith("piElements") || name.endsWith("untimeElements")) {
        artifacts.clear()
    }
    if (name == "apiElements" || name == "runtimeElements") {
        outgoing.artifact(testngAllJar) {
            classifier = null
        }
    }
}
//</editor-fold>
