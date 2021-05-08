package com.wblei.plugin.widget

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import java.io.File
import javax.lang.model.element.Modifier.PRIVATE

class RecyclerView : Widget {
  
  override fun constructLayout(): String {
    val res = """
  <androidx.recyclerview.widget.RecyclerView
      android:id="@+id/${getWidgetResId()}"
      android:layout_width="match_parent"
      android:layout_height="match_parent"
      tools:layout_editor_absoluteX="315dp"
      tools:layout_editor_absoluteY="421dp"
      app:layout_constraintBottom_toBottomOf="parent"
      app:layout_constraintLeft_toLeftOf="parent"
      app:layout_constraintRight_toRightOf="parent"
      app:layout_constraintTop_toTopOf="parent" />
      """
    return res
  }
  
  // generate resource id by the current nano time.
  private val resId = "tv${System.nanoTime()}"
  
  override fun getWidgetResId(): String {
    return resId
  }
  
  /**
   * add a setupText() method for TextView widget.
   */
  constructor(outDir: File, stringPrefix: String, typeBuilder: TypeSpec.Builder,
   rClass: ClassName, nextActivityClazzName: StringBuilder) {
    val methodName = "aa${System.nanoTime()}"
    val methodBuilder = MethodSpec.methodBuilder("$methodName")
     .addModifiers(PRIVATE)
     .addStatement("\$T rv = findViewById(\$T.id.${resId});",
      ClassName.get("androidx.recyclerview.widget", "RecyclerView"), rClass)
  
    if (!nextActivityClazzName.isNullOrEmpty()) {
      val intentStr = "\$T intent = new Intent(this, $nextActivityClazzName.class);"
      methodBuilder.addStatement(
       """
         rv.setOnClickListener(view -> {
            $intentStr
            startActivity(intent);
          })
       """.trimIndent(), ClassName.get("android.content", "Intent")
      )
    }
    
    typeBuilder.addMethod(methodBuilder.build())
    
    val methodSpecs = typeBuilder.methodSpecs
    // insert and setupText() method in onCreate() method.
    for (method in methodSpecs) {
      if (method.name == "onCreate") {
        val newMethod = method.toBuilder().addStatement("$methodName()").build()
        methodSpecs.remove(method)
        methodSpecs.add(0, newMethod)
        break
      }
    }
  }
}
