package com.wblei.plugin

import org.gradle.api.tasks.Input

open class MjpoetConfig {
  @Input var variants: ArrayList<String> = arrayListOf()
  @Input var packageBase:String = ""
  @Input var packageCount: Int = 0
  @Input var activityCountPerPkg: Int = 0
  @Input var excludeActivityJavaFile:Boolean = false
  @Input var methodCountPerClass = 0
  @Input var otherCountPerPackage = 0
  @Input var resPrefix: String = "mj_"
  @Input var drawableCount: Int = 0
  @Input var stringCount: Int = 0
  
}