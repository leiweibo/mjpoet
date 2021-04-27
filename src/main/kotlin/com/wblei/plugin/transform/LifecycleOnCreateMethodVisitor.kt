package com.wblei.plugin.transform

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Opcodes.*

class LifecycleOnCreateMethodVisitor(mv: MethodVisitor) : MethodVisitor(Opcodes.ASM4, mv) {
  
  //方法开始,此处可在方法开始插入字节码
  override fun visitCode() {
    super.visitCode()
    println("visit code")
    mv.visitLdcInsn("MainActivity");
    mv.visitLdcInsn("ttt");
    mv.visitMethodInsn(
     INVOKESTATIC,
     "android/util/Log",
     "i",
     "(Ljava/lang/String;Ljava/lang/String;)I",
     false
    );
    mv.visitInsn(POP);
    
  }
  
  //指令操作,这里可以判断拦截return,并在方法尾部插入字节码
  override fun visitInsn(opcode: Int) {
    if (opcode == ARETURN || opcode == RETURN) {
      mv.visitLdcInsn("MainActivity");
      mv.visitLdcInsn("tttInsn");
      mv.visitMethodInsn(
       INVOKESTATIC,
       "android/util/Log",
       "i",
       "(Ljava/lang/String;Ljava/lang/String;)I",
       false
      );
      mv.visitInsn(POP);
    }
    super.visitInsn(opcode)
  }
  
  //方法栈深度
  override fun visitMaxs(maxStack: Int, maxLocals: Int) {
    super.visitMaxs(maxStack, maxLocals)
  }
  
  //方法结束回调
  override fun visitEnd() {
    super.visitEnd()
  }
}