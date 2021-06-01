package buildlogic

import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.the

class OptionalFeatureBuilder(
    private val featureName: String,
    private val dependencyHandler: DependencyHandler
) {
    fun platform(dependencyNotation: Any) =
        dependencyHandler.platform(dependencyNotation)

    fun api(dependencyNotation: Any) {
        dependencyHandler.add("${featureName}Api", dependencyNotation)
    }

    fun implementation(dependencyNotation: Any) {
        dependencyHandler.add("${featureName}Implementation", dependencyNotation)
    }
}

open class OptionalFeatures(private val project: Project) {
    private val _features = mutableSetOf<String>()
    val features: Set<String> = _features

    fun create(name: String, builder: OptionalFeatureBuilder.() -> Unit) {
        project.the<JavaPluginExtension>().registerFeature(name) {
            val sourceSets: SourceSetContainer by project
            usingSourceSet(sourceSets["main"])
        }

        OptionalFeatureBuilder(name, project.dependencies).builder()
        _features += name

        // This is to include all testng modules (even optional) to -all.jar
        project.configurations.named("shadedDependencyFullRuntimeClasspath") {
            extendsFrom(project.configurations["${name}Api"])
            extendsFrom(project.configurations["${name}Implementation"])
        }

        // By default Gradle adds the jar as artifact, however, we won't need it
        // We'll put merged jar later as a main artifact
        project.configurations {
            named("${name}ApiElements") {
                artifacts.clear()
                // The feature do not provide their own classes or resources
                // All the feature resources would come from the dependencies
                outgoing.variants.removeIf { it.name == "classes" || it.name == "resources" }
            }
            named("${name}RuntimeElements") {
                artifacts.clear()
                outgoing.variants.removeIf { it.name == "classes" || it.name == "resources" }
            }
        }
    }
}
