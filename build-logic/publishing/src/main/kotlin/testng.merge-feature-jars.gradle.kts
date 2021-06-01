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
}

val shadedDependencyFullRuntimeClasspath by configurations.creating {
    description = "Resolves the list of shadedDependencyElements to testng and external dependencies"
    isCanBeConsumed = false
    isCanBeResolved = true
    isVisible = false
    extendsFrom(shadedDependencyElements)
    attributes {
        attribute(Category.CATEGORY_ATTRIBUTE, Category.LIBRARY)
        attribute(Usage.USAGE_ATTRIBUTE, Usage.JAVA_RUNTIME)
    }
}

val jarsToMerge by configurations.creating {
    description = "Resolves the list of testng modules to include into -all jar"
    isCanBeConsumed = false
    isCanBeResolved = true
    isTransitive = false
    attributes {
        attribute(Category.CATEGORY_ATTRIBUTE, Category.LIBRARY)
        attribute(Usage.USAGE_ATTRIBUTE, Usage.JAVA_RUNTIME)
    }
    withDependencies {
        // Clear any user-added-by-mistake dependencies
        clear()
        // Identifies TestNG projects in shadedDependencyFullRuntimeClasspath dependency tree
        addAll(
            shadedDependencyFullRuntimeClasspath.incoming.resolutionResult.allDependencies
                .asSequence()
                .filter { !it.isConstraint }
                .filterIsInstance<ResolvedDependencyResult>()
                .mapNotNull { resolved ->
                    val id = resolved.selected.id as? ProjectComponentIdentifier ?: return@mapNotNull null

                    val category = resolved.resolvedVariant.attributes.run {
                        keySet().firstOrNull { it.name == Category.CATEGORY_ATTRIBUTE.name }?.let { getAttribute(it) }
                    }

                    project.dependencies.create(project(id.projectPath)).let {
                        when (category) {
                            Category.REGULAR_PLATFORM -> project.dependencies.platform(it)
                            Category.LIBRARY -> it
                            else -> throw IllegalStateException("Unexpected dependency type $category for id $id")
                        }
                    }
                }
        )
    }
}

val shadedDependencyJavadocClasspath by configurations.creating {
    description = "Resolves a runtime classpath of the aggregated -all dependenices"
    isCanBeConsumed = false
    isCanBeResolved = true
    isVisible = false
    extendsFrom(jarsToMerge)
    extendsFrom(configurations["compileClasspath"])
    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, Usage.JAVA_RUNTIME)
        attribute(Category.CATEGORY_ATTRIBUTE, Category.LIBRARY)
        attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, LibraryElements.JAR)
        attribute(Bundling.BUNDLING_ATTRIBUTE, Bundling.EXTERNAL)
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
    isTransitive = false // jarsToMerge is a full set of modules, so no need to have transitivity here
    extendsFrom(jarsToMerge)
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
