/*************************************************************************
 * ULLINK CONFIDENTIAL INFORMATION
 * _______________________________
 *
 * All Rights Reserved.
 *
 * NOTICE: This file and its content are the property of Ullink. The
 * information included has been classified as Confidential and may
 * not be copied, modified, distributed, or otherwise disseminated, in
 * whole or part, without the express written permission of Ullink.
 ************************************************************************/
package com.itiviti

import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class DotnetPluginSpec extends Specification {

    def "Project is restored and parsed correctly in evaluate()"() {
        setup:
        def project = ProjectBuilder.builder()
                .build()
        project.plugins.apply('com.itiviti.dotnet')

        def pluginExtension = project.extensions.getByType(DotnetPluginExtension)
        pluginExtension.projectName = 'core'
        pluginExtension.workingDir = new File(this.class.getResource('project').toURI())

        when:
        project.evaluate()

        then:
        pluginExtension.allProjects.size() == 2

        pluginExtension.mainProject.projectName == 'core'
        pluginExtension.mainProject.targetPath != null
        pluginExtension.mainProject.projectDirectory != null
        pluginExtension.mainProject.projectFile != null
        pluginExtension.mainProject.packageReferences.size() == 3
        pluginExtension.mainProject.outputPaths.size() == 4
        pluginExtension.mainProject.inputFiles.size() == 8
        [ DotnetProject.BuildAction.Content,
          DotnetProject.BuildAction.ApplicationDefinition,
          DotnetProject.BuildAction.None,
          DotnetProject.BuildAction.EmbeddedResource,
          DotnetProject.BuildAction.Page,
          DotnetProject.BuildAction.Compile,
          DotnetProject.BuildAction.Resource].each {
            assert pluginExtension.mainProject.getSources(it).size() == 1
        }
    }
}