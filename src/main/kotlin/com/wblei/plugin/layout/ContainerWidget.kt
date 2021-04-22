package com.wblei.plugin.layout

import com.wblei.plugin.widget.Widget

interface ContainerWidget {
  fun constructLayout(widgets: MutableList<Widget>):String
}