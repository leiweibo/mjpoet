package com.wblei.plugin

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeSpec
import com.wblei.plugin.layout.ConstraintLayout
import com.wblei.plugin.layout.FrameLayout
import com.wblei.plugin.layout.LinearLayout
import com.wblei.plugin.layout.RelativeLayout
import com.wblei.plugin.widget.Button
import com.wblei.plugin.widget.ImageView
import com.wblei.plugin.widget.RecyclerView
import com.wblei.plugin.widget.TextView
import com.wblei.plugin.widget.Widget
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectories
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.FileWriter
import java.io.IOException
import javax.lang.model.element.Modifier

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
    if (outDir.exists()) {
      outDir.delete()
    }
    generateJavaClasses()
  }
  
  private var statisticPackageCount = 0
  private var statisticActivityCount = 0
  private var statisticWidgetCountInLayout = 0
  private var statisticHelperCount = 0
  private var drawableCount = 0
  
  /**
   * generate the classes.
   */
  private fun generateJavaClasses() {
    var packageCnt = (3..5).random()
    statisticPackageCount = packageCnt
    for (i in 1..packageCnt) {
      generateClassInSinglePkg()
    }
    
    println("package count: $statisticPackageCount")
    println("activity count: $statisticActivityCount")
    println("widget count: $statisticWidgetCountInLayout")
    println("helper class count: $statisticHelperCount")
    println("drawable count: $drawableCount")
  }
  
  /**
   * generate the single class base on the class name and layout name parameters.
   */
  private fun generateClassInSinglePkg() {
    
    var javaDir = File(outDir, "java")
    val packageName = "${config.packageBase}.P${System.currentTimeMillis()}"
    
    var activitiesCnt = (3..6).random()
    statisticActivityCount += activitiesCnt
    val activityNameList = mutableListOf<String>()
    // Generate 5-10 random activities in every package.
    for (i in 1..activitiesCnt) {
  
      val activityPrefix = ('A'..'Z').map { it }.shuffled().subList(0, (1..3).random()).joinToString("")
      val currentTime = System.nanoTime()
      val className = "$activityPrefix${currentTime}Activity"
      activityNameList.add(className)
    }
    println("the activity name list: $activityNameList")
    activityNameList.sort()
    for (i in 1..activitiesCnt) {
  
      val currentTime = System.nanoTime()
      var className = activityNameList[i - 1]
      var nextActivityName:String = if (i == activitiesCnt) ""  else activityNameList[i]
      
      val layoutName = "${config.resPrefix}_${currentTime}"
  
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
      generateLayout(layoutName, typeBuilder, nextActivityName)
      writeManifestFile("${packageName}.$className")
  
      generateHelperClasses(packageName, typeBuilder)
  
      val fileBuilder = JavaFile.builder(packageName, typeBuilder.build())
      fileBuilder.build().writeTo(javaDir)
    }
  }
  
  
  
  /**
   * generate the layout and add related get the widget code into the Activity class.
   * @param layoutName: the layout name
   * @param typeBuilder: the typebuilder that add more method base on the added widget.
   * @param nextActivityClazzName: the next activity activity, if it's not empty or null, will add a
   *                               statement of setOnClickListener()
   */
  private fun generateLayout(layoutName: String, typeBuilder: TypeSpec.Builder, nextActivityClazzName: String) {
    val resFile = File(outDir, "res/layout/${layoutName}.xml")
    if (!resFile.parentFile.exists()) {
      resFile.parentFile.mkdirs()
    }
    if (!resFile.exists()) {
      resFile.createNewFile()
    }
    
    val rClassName = ClassName.get(appPackageName, "R")
    val writer = FileWriter(resFile)
     // random container with random child view.
    try {
      when((0..3).random()) {
        0 ->  {
          val constraintLayout = RelativeLayout.constructLayout(getChildViews(typeBuilder, rClassName, nextActivityClazzName))
          writer.write(constraintLayout)
        }
        1 ->  {
          val constraintLayout = LinearLayout.constructLayout(getChildViews(typeBuilder, rClassName, nextActivityClazzName))
          writer.write(constraintLayout)
        }
        2-> {
          config.drawableCount
          val constraintLayout = FrameLayout.constructLayout(getChildViews(typeBuilder, rClassName, nextActivityClazzName))
          writer.write(constraintLayout)
        }
        3 -> {
          val constraintLayout = ConstraintLayout.constructLayout(getChildViews(typeBuilder, rClassName, nextActivityClazzName))
          writer.write(constraintLayout)
        }
      }
      

    } catch (e: IOException) {
      print(e.message)
    } finally {
      writer?.close()
    }
  }
  
  // generate random views
  private fun getChildViews(typeBuilder: TypeSpec.Builder, rClassName:ClassName, nextActivityClazzName: String): MutableList<Widget> {
    val randomChildCnt = (2..5).random()
    val children = mutableListOf<Widget>()
    statisticWidgetCountInLayout += randomChildCnt
    val nextActivityClazzNameInBuilder = StringBuilder(nextActivityClazzName)
    for (i in 1..randomChildCnt) {
      val randomPos = (0..3).random()
      when(randomPos) {
        0 -> {
              children.add(TextView(outDir, config.resPrefix, typeBuilder, rClassName, nextActivityClazzNameInBuilder))
        }
        1 -> {
          children.add(Button(outDir, config.resPrefix, typeBuilder, rClassName, nextActivityClazzNameInBuilder))
        }
        2 -> {
          if (drawableCount < config.drawableCount) {
            drawableCount ++
            children.add(ImageView(outDir, config.resPrefix, typeBuilder, rClassName, nextActivityClazzNameInBuilder))
          } else {
            children.add(TextView(outDir, config.resPrefix, typeBuilder, rClassName, nextActivityClazzNameInBuilder))
          }
        }
        3 -> {
          children.add(RecyclerView(outDir, config.resPrefix, typeBuilder, rClassName, nextActivityClazzNameInBuilder))
        }
      }
  
      nextActivityClazzNameInBuilder.clear()
    }
    return children
  }
  
  /**
   * add the activity declaration into android manifst file.
   */
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
  
  /**
   * Generate helper by random, 20 at most.
   */
  private fun generateHelperClasses(packageName: String, activityTypeBuilder: TypeSpec.Builder) {
    val helpClazzCnt = (1..2).random()
    statisticHelperCount += helpClazzCnt
    for (i in 0..helpClazzCnt) {
      var javaDir = File(outDir, "java")
      val methodName = "a${System.nanoTime()}"
      val className = "M${System.nanoTime()}Helper"
  
      val (paramList, defaultValueList, parameterNameList) = generateParameterSpecList()
  
      val stateSb = StringBuilder()
      for (name in parameterNameList) {
        stateSb.append("System.out.println($name);\n")
      }
  
      val typeBuilder = TypeSpec.classBuilder(className)
      typeBuilder.superclass(ClassName.get("android.app", "Activity"))
      typeBuilder.addModifiers(Modifier.PUBLIC)
  
      val builder: MethodSpec.Builder = MethodSpec.methodBuilder(methodName)
       .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
       .addParameters(paramList)
       .addStatement("$stateSb")
      
      GenerateHelper.generateMethods(builder)
      
      typeBuilder.addMethod(
       builder.build())
  
      val fileBuilder = JavaFile.builder(packageName, typeBuilder.build())
      fileBuilder.build().writeTo(javaDir)
  
  
      val methodSpecs = activityTypeBuilder.methodSpecs
      // insert and setupText() method in onCreate() method.
      for (method in methodSpecs) {
        if (method.name == "onCreate") {
          val newMethod = method.toBuilder().addStatement(
           "${packageName}.${className}.$methodName(${defaultValueList})").build()
          methodSpecs.remove(method)
          methodSpecs.add(0, newMethod)
          break
        }
      }
    }
  }
  
  private fun generateParameterSpecList(): Triple<MutableList<ParameterSpec>, String, MutableList<String>> {
    val paramCount = System.currentTimeMillis() % 4
    val result = mutableListOf<ParameterSpec>()
    val defaultValueList = mutableListOf<Any>()
    val parameterNameList = mutableListOf<String>()
    for (i in 0..paramCount) {
      val customParameter = when(val pos:Int =  (System.nanoTime() % 5).toInt()) {
        0 -> CustomParameter(String::class.java, "s", "str${System.nanoTime()}")
        1 -> CustomParameter(Boolean::class.java, false, "boo${System.nanoTime()}")
        2 -> CustomParameter(Short::class.java, 1, "soo${System.nanoTime()}")
        3 -> CustomParameter(Long::class.java, 1L, "ooo${System.nanoTime()}")
        4 -> CustomParameter(Int::class.java, 0, "ioo${System.nanoTime()}s")
        else -> CustomParameter(Float::class.java, 0f, "foo${System.nanoTime()}")
      }
      if (customParameter.clazz == String::class.java) {
        val tmp = "\"${customParameter.defaultValue}\""
        defaultValueList.add(tmp)
      } else if(customParameter.clazz == Short::class.java) {
        val tmp = "(short)${customParameter.defaultValue}"
        defaultValueList.add(tmp)
      } else {
        defaultValueList.add(customParameter.defaultValue)
      }
      result.add(ParameterSpec.builder(customParameter.clazz, customParameter.parameterName).build())
      parameterNameList.add(customParameter.parameterName)
    }
    return Triple(result, defaultValueList.joinToString(","), parameterNameList)
  }
  
  class  CustomParameter<T>(clazz: Class<T>, defaultValue: Any, parameterName: String) {
    var clazz: Class<T> = clazz
    var defaultValue = defaultValue
    val parameterName = parameterName
  }
}