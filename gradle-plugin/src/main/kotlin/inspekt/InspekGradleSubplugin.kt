package inspekt

import com.google.auto.service.AutoService
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.compile.AbstractCompile
import org.gradle.tooling.provider.model.ToolingModelBuilder
import org.gradle.tooling.provider.model.ToolingModelBuilderRegistry
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonOptions
import org.jetbrains.kotlin.gradle.plugin.*

@AutoService(KotlinGradleSubplugin::class)
class InspekGradleSubplugin : KotlinCompilerPluginSupportPlugin, @Suppress("DEPRECATION") // implementing to fix KT-39809
KotlinGradleSubplugin<AbstractCompile> {

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean {
        return true
    }

    override fun apply(target: Project) {
        target.extensions.create("inspekt", InspektGradleExtension::class.java)
    }

    override fun apply(
        project: Project,
        kotlinCompile: AbstractCompile,
        javaCompile: AbstractCompile?,
        variantData: Any?,
        androidProjectHandler: Any?,
        kotlinCompilation: KotlinCompilation<KotlinCommonOptions>?
    ): List<SubpluginOption> {
        val extension = project.extensions.findByType(InspektGradleExtension::class.java)
            ?: InspektGradleExtension()

        val enabledOption = SubpluginOption(key = "enabled", value = extension.enabled.toString())
        return listOf(enabledOption)
    }

    override fun getCompilerPluginId(): String = "inspekt"

    override fun getPluginArtifact(): SubpluginArtifact =
        SubpluginArtifact(
            groupId = "inspekt",
            artifactId = "compiler",
            version = "0.0.1" // remember to bump this version before any release!
        )

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        val project = kotlinCompilation.target.project
        val extension = project.extensions.getByType(InspektGradleExtension::class.java)
        return project.provider {
            listOf(
                SubpluginOption(key = "string", value = extension.enabled.toString())
            )
        }
    }

    override fun isApplicable(project: Project, task: AbstractCompile): Boolean =
        project.plugins.hasPlugin(InspektGradlePlugin::class.java)
}