package com.wblei.plugin

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectories
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.lang.model.element.Modifier

open class MjpoetTask : DefaultTask() {
  @Nested
  var config: MjpoetConfig = MjpoetConfig()
  
  @Input
  var appPackageName: String? = ""
  
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
    val className = "TestActivity"
    val layoutName = "activity_test"
    
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
    
    val fileBuilder = JavaFile.builder(packageName, typeBuilder.build())
    fileBuilder.build().writeTo(javaDir)
  }
  
  private fun generateLayout(layoutName: String) {
    val resFile = File(outDir, "res/layout/${layoutName}.xml")
    if (!resFile.parentFile.exists()) {
      resFile.parentFile.mkdirs()
    }
    if (!resFile.exists()) {
      resFile.createNewFile()
    }
    
    
  }
}