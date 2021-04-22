package com.wblei.plugin.widget

interface Widget {
  fun constructLayout():String
  
  fun getWidgetResId(): String
}