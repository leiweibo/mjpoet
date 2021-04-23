package com.wblei.plugin.transform

import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent.ContentType
import com.android.build.api.transform.QualifiedContent.Scope
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import java.io.File
import java.io.IOException


class MjPoetTransform : Transform() {
  override fun getName(): String {
    return "MjPoetTransform"
  }
  
  override fun getInputTypes(): MutableSet<ContentType> {
    return TransformManager.CONTENT_CLASS
  }
  
  override fun isIncremental(): Boolean {
    return false
  }
  
  override fun getScopes(): MutableSet<in Scope> {
    return TransformManager.SCOPE_FULL_PROJECT
  }
  
  @Throws(TransformException::class, InterruptedException::class, IOException::class)
  override fun transform(transformInvocation: TransformInvocation) {
    super.transform(transformInvocation)
    //当前是否是增量编译(由isIncremental() 方法的返回和当前编译是否有增量基础)
    val isIncremental = transformInvocation.isIncremental
    //消费型输入，可以从中获取jar包和class文件夹路径。需要输出给下一个任务
    val inputs = transformInvocation.inputs
    //OutputProvider管理输出路径，如果消费型输入为空，你会发现OutputProvider == null
    val outputProvider = transformInvocation.outputProvider
    for (input in inputs) {
      for (jarInput in input.jarInputs) {
        val dest: File = outputProvider.getContentLocation(
         jarInput.file.absolutePath,
         jarInput.contentTypes,
         jarInput.scopes,
         Format.JAR)
        //将修改过的字节码copy到dest，就可以实现编译期间干预字节码的目的了
        FileUtils.copyFile(jarInput.file, dest)
      }
      for (directoryInput in input.directoryInputs) {
        val dest: File = outputProvider.getContentLocation(directoryInput.name,
         directoryInput.contentTypes, directoryInput.scopes,
         Format.DIRECTORY)
        //将修改过的字节码copy到dest，就可以实现编译期间干预字节码的目的了
        FileUtils.copyDirectory(directoryInput.file, dest)
      }
    }
  }
}