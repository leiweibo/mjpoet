package com.wblei.plugin

import groovy.lang.Closure

import org.gradle.api.Project
import org.gradle.internal.reflect.Instantiator

open class MjpoetExt {
  lateinit var config: MjpoetConfig
  var project: Project
  
  constructor(instantiator: Instantiator, project: Project) {
    this.project = project
  }
  
  fun config(closure: Closure<MjpoetConfig>) {
    var tmpConfig: MjpoetConfig = MjpoetConfig()
    project.configure(tmpConfig, closure)
  }
}