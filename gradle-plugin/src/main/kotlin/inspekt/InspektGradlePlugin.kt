package inspekt

import org.gradle.api.Project

class InspektGradlePlugin : org.gradle.api.Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create(
            "inspekt",
            InspektGradleExtension::class.java
        )
    }
}
