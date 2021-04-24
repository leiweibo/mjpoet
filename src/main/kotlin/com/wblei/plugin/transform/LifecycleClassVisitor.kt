package com.wblei.plugin.transform

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class LifecycleClassVisitor(cv: ClassVisitor) : ClassVisitor(Opcodes.ASM5, cv), Opcodes {

    private var mClassName: String? = null

    override fun visit(version: Int, access: Int, name: String, signature: String,
                       superName: String, interfaces: Array<String>) {
        println("LifecycleClassVisitor : visit -----> started ：$name")
        this.mClassName = name
        super.visit(version, access, name, signature, superName, interfaces)
    }

    override fun visitMethod(access: Int, name: String, desc: String,
                             signature: String, exceptions: Array<String>): MethodVisitor {
        println("LifecycleClassVisitor : visitMethod : $name")
        val mv = cv.visitMethod(access, name, desc, signature, exceptions)
        //匹配FragmentActivity
        if ("android/support/v4/app/FragmentActivity" == this.mClassName) {
            if ("onCreate" == name) {
                //处理onCreate
                return LifecycleOnCreateMethodVisitor(mv)
            }
//            else if ("onDestroy" == name) {
//                //处理onDestroy
//                return LifecycleOnDestroyMethodVisitor(mv)
//            }
        }
        return mv
    }

    override fun visitEnd() {
        println("LifecycleClassVisitor : visit -----> end")
        super.visitEnd()
    }
    // https://blog.csdn.net/zhongweijian/article/details/7861460
    // https://www.jianshu.com/p/16ed4d233fd1
}