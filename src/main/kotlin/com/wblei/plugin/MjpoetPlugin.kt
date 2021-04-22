/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package com.wblei.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import groovy.util.Node
import groovy.util.XmlParser

import org.gradle.api.Project
import org.gradle.api.Plugin
import java.io.File
import java.lang.IllegalArgumentException

/**
 * A simple 'hello world' plugin.
 */
class MjpoetPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        var android = project.extensions.getByType(AppExtension::class.java)
        android ?: throw IllegalArgumentException("The plugin can only used in android project.")
        
        // create the an extension for plugin.
        val mjpoetConfig = project.extensions.create("mjpoet", MjpoetConfig::class.java)
        project.afterEvaluate {
            android.applicationVariants.all {
                val mjpoet = project.extensions.getByName("mjpoet") as MjpoetConfig
                if (it.name in mjpoet.variants) {
    
//                    print("hello wrold......\n")
//                    println(mjpoet.packageBase)
//                    println(it.name)
//                    println((it as ApplicationVariant).sourceSets)
                    
                    var dir = File(project.buildDir, "generated/source/mj/${it.name}")
                    var javaDir = File(dir, "java")
                    var resDir = File(dir, "res")
                    var manifestFile = File(dir, "AndroidManifest.xml")
                    
                    val mjTask = project.tasks.register("generate${it.name}MJCode", MjpoetTask::class.java) { task ->
                        task.config = mjpoetConfig
                        task.outDir = dir
                        task.appPackageName = getPackageName((it as ApplicationVariant))
                    }
                    it.registerJavaGeneratingTask(mjTask.get(), javaDir)
                    it.registerGeneratedResFolders(project.files(resDir).builtBy(mjTask))
                }
            }
        }
    }
    
    private fun getPackageName(variant: ApplicationVariant):String? {
        val sourceSets = variant.sourceSets
        var packageName : String? = null
        for (sourceSet in sourceSets) {
            if (sourceSet.manifestFile.exists()) {
                val xmlParser = XmlParser()
                val node: Node = xmlParser.parse(sourceSet.manifestFile)
                packageName = node.attribute("package") as String
                if (packageName.isNotEmpty()) {
                    break
                }
            }
        }
        return packageName
    }
}
