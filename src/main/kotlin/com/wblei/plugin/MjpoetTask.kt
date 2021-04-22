package com.wblei.plugin

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import com.wblei.plugin.layout.ConstraintLayout
import com.wblei.plugin.widget.TextView
import com.wblei.plugin.widget.Widget
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectories
import org.gradle.api.tasks.TaskAction
import org.gradle.platform.base.TypeBuilder
import java.io.File
import java.io.FileWriter
import java.io.IOException
import javax.lang.model.element.Modifier
import javax.lang.model.element.Modifier.PRIVATE

open class MjpoetTask : DefaultTask() {
  @Nested
  var config: MjpoetConfig = MjpoetConfig()
  
  @Input
  var appPackageName: String? = ""
  
  // the generated files out put folder.
  @OutputDirectories
  lateinit var outDir: File
  
  @TaskAction
  fun start() {
    print("the package base name is: ${config.packageBase}")
    
    if (outDir.exists()) {
      outDir.delete()
    }
    generateJavaClasses()
  }
  
  /**
   * generate the classes.
   */
  private fun generateJavaClasses() {
      val currentTime = System.nanoTime()
      val className = "AA${currentTime}Activity"
      val layoutName = "${config.resPrefix}_${currentTime}"
  
      generateSingleClass(className, layoutName)
  }
  
  /**
   * generate the single class base on the class name and layout name parameters.
   * @param className: class name
   * @param layoutName: layout name
   */
  private fun generateSingleClass(className: String, layoutName: String) {
    
    var javaDir = File(outDir, "java")
    val packageName = "${config.packageBase}"
    val typeBuilder = TypeSpec.classBuilder(className)
    typeBuilder.superclass(ClassName.get("android.app", "Activity"))
    typeBuilder.addModifiers(Modifier.PUBLIC)
    val bundleClassName = ClassName.get("android.os", "Bundle")
    val rClassName = ClassName.get(appPackageName, "R")
    typeBuilder.addMethod(MethodSpec.methodBuilder("onCreate")
     .addModifiers(Modifier.PROTECTED)
     .addParameter(bundleClassName, "savedInstanceState")
     .addStatement("super.onCreate(savedInstanceState)")
     .addStatement("setContentView(${rClassName}.layout.$layoutName)")
     .build())
  
    // generate the layout and add setupXX method in the Activity.
    generateLayout(layoutName, typeBuilder)
    writeManifestFile("${packageName}.$className")
    
    val fileBuilder = JavaFile.builder(packageName, typeBuilder.build())
    fileBuilder.build().writeTo(javaDir)
  }
  
  /**
   * generate the layout and add related get the widget code into the Activity class.
   * @param layoutName: the layout name
   * @param typeBuilder: the typebuilder that add more method base on the added widget.
   */
  private fun generateLayout(layoutName: String, typeBuilder: TypeSpec.Builder) {
    val resFile = File(outDir, "res/layout/${layoutName}.xml")
    if (!resFile.parentFile.exists()) {
      resFile.parentFile.mkdirs()
    }
    if (!resFile.exists()) {
      resFile.createNewFile()
    }
  
    val rClassName = ClassName.get(appPackageName, "R")
    val writer = FileWriter(resFile)
    val textView1 = TextView(outDir, config.resPrefix, typeBuilder, rClassName)
    val textView2 = TextView(outDir, config.resPrefix, typeBuilder, rClassName)
    
    try {
      val constraintLayout = ConstraintLayout.constructLayout(mutableListOf(textView1, textView2))
      writer.write(constraintLayout)
    } catch (e: IOException) {
      print(e.message)
    } finally {
      writer?.close()
    }
  }
  
  private fun writeManifestFile(fullActivityName: String) {
  
    var androidManifest = File(outDir, "AndroidManifest.xml")
    if (!androidManifest.parentFile.exists()) {
      androidManifest.parentFile.mkdirs()
    }
    if (!androidManifest.exists()) {
      androidManifest.createNewFile()
      androidManifest.writeText("""
                        <manifest xmlns:android="http://schemas.android.com/apk/res/android">
                             <application>
                             
                             </application>
                        </manifest>
                        """.trimIndent())
    }
    
    val sb = StringBuilder()
    androidManifest.forEachLine {
      if (it.contains("</application>")) {
        sb.append("\n\t\t<activity android:name=\"$fullActivityName\" />\n")
      }
      sb.append("$it\n")
      if (it.contains("<application>")) {
        sb.append("\n")
      }
    }
    androidManifest.writeText(sb.toString())
  }
}