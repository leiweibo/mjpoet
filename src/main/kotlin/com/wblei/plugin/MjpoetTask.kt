package com.wblei.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.TaskAction

class MjpoetTask : DefaultTask() {
  @Nested val config: MjpoetConfig = MjpoetConfig()
  
  @TaskAction fun start() {
    print("the package base name is: ${config.packageBase}")
  }
}