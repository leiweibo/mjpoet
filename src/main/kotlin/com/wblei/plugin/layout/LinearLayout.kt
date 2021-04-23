package com.wblei.plugin.layout

import com.wblei.plugin.widget.Widget

object LinearLayout : ContainerWidget {
  
  override fun constructLayout(list: MutableList<Widget>):String {
    var finalLayout = ""
    for (widget in list) {
      finalLayout += widget.constructLayout()
    }
    
    val xmlRes = """
<LinearLayout
      xmlns:android="http://schemas.android.com/apk/res/android"
      xmlns:tools="http://schemas.android.com/tools"
      xmlns:app="http://schemas.android.com/apk/res-auto"
      android:layout_width="match_parent"
      android:layout_height="match_parent"
      android:orientation="horizontal"
      tools:layout_editor_absoluteX="142dp"
      tools:layout_editor_absoluteY="249dp">>
  $finalLayout
</LinearLayout>
    """
    return xmlRes
  }
}