import buildlogic.OptionalFeatures
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `java-base`
    `reporting-base`
    id("testng.published-java-library")
}

(the<JavaPluginExtension>() as ExtensionAware).extensions
    .create<OptionalFeatures>("optionalFeatures", project)

inline fun <reified T : Named> AttributeContainer.attribute(attr: Attribute<T>, value: String) =
    attribute(attr, objects.named(value))

val shadedDependencyElements by configurations.creating {
    description = "Declares which modules to aggregate into ...-all.jar"
    isCanBeConsumed = false
    isCanBeResolved = false
    isVisible = false
}

val shadedDependencyJavadocClasspath by configurations.creating {
    description = "Resolves a runtime classpath of the aggregated -all dependenices"
    isCanBeConsumed = false
    isCanBeResolved = true
    isVisible = false
    extendsFrom(shadedDependencyElements)
    extendsFrom(configurations["compileClasspath"])
    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, Usage.JAVA_RUNTIME)
        attribute(Category.CATEGORY_ATTRIBUTE, Category.LIBRARY)
        attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, LibraryElements.JAR)
        attribute(Bundling.BUNDLING_ATTRIBUTE, Bundling.EXTERNAL)
    }
}

fun Configuration.keepOnlyTestNgModules() {
    resolutionStrategy.dependencySubstitution.all {
        // prune third-party dependencies, so testng-all.jar contains only testng classes
        if (requested !is ProjectComponentSelector) {
            // It is not clear how to remove dependency, however,
            // replacing everything with collections seems to be harmless
            useTarget(project(":testng-collections"))
        }
    }
}

val jarsToMerge by configurations.creating {
    description = "Resolves the list of dependencies to include into -all jar"
    isCanBeConsumed = false
    isCanBeResolved = true
    extendsFrom(shadedDependencyElements)
    keepOnlyTestNgModules()
    attributes {
        attribute(Category.CATEGORY_ATTRIBUTE, Category.LIBRARY)
        attribute(Usage.USAGE_ATTRIBUTE, Usage.JAVA_API)
    }
}

val mergedJar by tasks.registering(ShadowJar::class) {
    group = LifecycleBasePlugin.BUILD_GROUP
    description = "Builds all-project jar (third-party dependencies are left as is)"
    configurations = listOf(jarsToMerge)
    archiveClassifier.set("all")
}

val sourcesToMerge by configurations.creating {
    description = "Resolves the list of source directories to include into sources-all jar"
    isCanBeConsumed = false
    isCanBeResolved = true
    extendsFrom(shadedDependencyElements)
    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, Usage.JAVA_RUNTIME)
        attribute(Category.CATEGORY_ATTRIBUTE, Category.DOCUMENTATION)
        attribute(DocsType.DOCS_TYPE_ATTRIBUTE, "source-folders")
    }
}

val mergedSourcesJar by tasks.registering(Jar::class) {
    from(sourcesToMerge.incoming.artifactView { lenient(true) }.files)
    archiveClassifier.set("sources-all")
}

val mergedJavadoc by tasks.registering(Javadoc::class) {
    description = "Generates an aggregate javadoc"
    group = LifecycleBasePlugin.BUILD_GROUP
    setSource(sourcesToMerge.incoming.artifactView { lenient(true) }.files)
    include("**/*.java")
    setDestinationDir(reporting.file("mergedJavadoc"))
    classpath = shadedDependencyJavadocClasspath
}

val mergedJavadocJar by tasks.registering(Jar::class) {
    description = "Generates an aggregate javadoc jar"
    group = LifecycleBasePlugin.BUILD_GROUP
    from(mergedJavadoc)
    archiveClassifier.set("javadoc-all")
}

// Configure merged artifacts for publication
configurations.named("sourcesElements") {
    artifacts.clear()
    outgoing.artifact(mergedSourcesJar) {
        classifier = "sources"
    }
}

configurations.named("javadocElements") {
    artifacts.clear()
    outgoing.artifact(mergedJavadocJar) {
        classifier = "javadoc"
    }
}

for (name in listOf("apiElements", "runtimeElements")) {
    configurations.named(name) {
        artifacts.clear()
        outgoing.artifact(mergedJar) {
            classifier = null
        }
        outgoing.variants.removeIf { it.name == "classes" || it.name == "resources" }
    }
}
