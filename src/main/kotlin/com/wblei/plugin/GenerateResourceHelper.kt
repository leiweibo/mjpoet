package com.wblei.plugin

import java.io.File
import java.io.FileReader
import java.io.FileWriter

/**
 * The helper class that generate resource.
 */
object GenerateResourceHelper {
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
      println("create file strings.xml: ${stringResFile.createNewFile()}")
      stringResFile.writeText("<resources>\n</resources>")
    }
    val stringName = "$prefix${System.nanoTime()}"
    val sb = StringBuilder()
    stringResFile.forEachLine {
      print("the content is: $it")
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
  fun generateDrawable(basedir: String, prefix: String) {
  
  }
}