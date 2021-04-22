package com.wblei.plugin

import com.squareup.javapoet.MethodSpec
import java.io.File
import java.util.Date
import javax.lang.model.element.Modifier

/**
 * The helper class that generate resource.
 */
object GenerateHelper {
  /**
   * Generate string
   * @param basedir the base dir of the string resource.
   * @param prefix: the prefix of the string in strings.xml
   */
  fun generateStringRes(basedir: File, prefix: String): String {
    // If the directory or the files not exists, create the directory and files.
    // and setup the <resources></resources>
    val stringResFile = File(basedir, "res/values/strings.xml")
    if (!stringResFile.parentFile.exists()) {
      stringResFile.parentFile.mkdirs()
    }
    if (!stringResFile.exists()) {
      stringResFile.createNewFile()
      stringResFile.writeText("<resources>\n</resources>")
    }
    val stringName = "$prefix${System.nanoTime()}"
    val sb = StringBuilder()
    stringResFile.forEachLine {
      if (it == "</resources>") {
        sb.append("\n\t<string name=\"$stringName\">${System.currentTimeMillis()}</string>\n")
      }
      sb.append("$it")
      if (it == "<resources>") {
        sb.append("\n")
      }
    }
    stringResFile.writeText(sb.toString())
    return stringName
  }
  
  /**
   * Generate drawable.
   * @param basedir the base dir of the drawable resource.
   * @param prefix the prefix of the drawable file.
   */
  fun generateDrawable(basedir: File, prefix: String): String {
    // If the directory or the files not exists, create the directory and files.
    // and setup the <resources></resources>
    val drawable = File(basedir, "res/drawable")
    if (!drawable.exists()) {
      drawable.mkdirs()
    }
    val drawableName = "${prefix}${System.nanoTime()}"
    val name = "${drawableName}.xml"
    val drawableFile = File(drawable, name)
    if (!drawableFile.exists()) {
      drawableFile.writeText(Drawable.res)
    }
    
    return drawableName
  }
  
  fun generateMethods(methodBuilder: MethodSpec.Builder) {
    var pos = (0..4).random()
    pos = 4
    when (pos) {
      0 ->
        methodBuilder.addStatement("long now = \$T.currentTimeMillis()", System::class.java)
         .beginControlFlow("if (\$T.currentTimeMillis() < now)", System::class.java)
         .addStatement("\$T.out.println(\$S)", System::class.java, "Time travelling, woo hoo!")
         .nextControlFlow("else if (\$T.currentTimeMillis() == now)", System::class.java)
         .addStatement("\$T.out.println(\$S)", System::class.java, "Time stood still!")
         .nextControlFlow("else")
         .addStatement("\$T.out.println(\$S)", System::class.java, "Ok, time still moving forward")
         .endControlFlow()
         .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
      1 ->
        methodBuilder
         .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
         .addCode(""
         + "int total = 0;\n"
         + "for (int i = 0; i < 10; i++) {\n"
         + "  total += i;\n"
         + "}\n")
      2 ->
        methodBuilder.beginControlFlow("try")
         .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
         .addStatement("throw new Exception(\$S)", "Failed")
         .nextControlFlow("catch (\$T e)", Exception::class.java)
         .addStatement("e.printStackTrace()")
         .endControlFlow()
      3 ->
        methodBuilder.returns(Date::class.java)
         .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
         .addStatement("return new \$T()", Date::class.java)
      4 ->
        methodBuilder.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
         .addStatement("\$T.out.println(\$S)", System::class.java, "Hello")
      
    }
  }
}