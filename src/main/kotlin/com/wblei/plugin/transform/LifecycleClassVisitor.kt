package com.wblei.plugin.transform

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class LifecycleClassVisitor(cv: ClassVisitor) : ClassVisitor(Opcodes.ASM5, cv), Opcodes {
    
    private var className: String? = null
    
    //类入口
    override fun visit(
     version: Int,
     access: Int,
     name: String?,
     signature: String?,
     superName: String?,
     interfaces: Array<out String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
        className = name
        println("visit name:$name")
    }
    
    //类中方法的入口
    override fun visitMethod(
     access: Int,
     name: String?,
     descriptor: String?,
     signature: String?,
     exceptions: Array<out String>?
    ): MethodVisitor {
        val result =  super.visitMethod(access, name, descriptor, signature, exceptions)
        println("visitMethod name:$name")
        if (className == "com/wblei/trygradle/MainActivity" && name == "onCreate") {//过滤需要操作的类名和方法名
            //替换成自定义的方法扫描
            return LifecycleOnCreateMethodVisitor(result)
        }
        return result
    }
    // https://blog.csdn.net/zhongweijian/article/details/7861460
    // https://www.jianshu.com/p/16ed4d233fd1
}