package com.itiviti.tasks

import com.itiviti.extensions.DotnetBuildExtension
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectories
import java.io.File

open class DotnetBuildTask: DotnetBaseTask("build") {

    @InputFiles
    val sources: ListProperty<File> = project.objects.listProperty(File::class.java)
            .convention (project.provider { getPluginExtension().allProjects.map { it.value.getInputFiles() }.flatten() })

    @OutputDirectories
    val destinations: ListProperty<File> = project.objects.listProperty(File::class.java)
            .convention (project.provider { getPluginExtension().allProjects.map { it.value.getOutputPaths() }.flatten() })

    init {
        sources.finalizeValueOnRead()
        destinations.finalizeValueOnRead()

        // explicitly restored in evaluation phase
        args("--no-restore")
        val buildExtension = getNestedExtension(DotnetBuildExtension::class.java)
        if (!buildExtension.version.isNullOrEmpty()) {
            args("-p:Version=${buildExtension.version}")
        }
        if (!buildExtension.packageVersion.isNullOrEmpty()) {
            args("-p:PackageVersion=${buildExtension.packageVersion}")
        }

        buildExtension.getProperties().forEach {
            args("-p:${it.key}=${it.value}")
        }
    }
}