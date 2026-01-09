package com.hello.mihe.app.launcher.view.adapter

import androidx.viewbinding.ViewBinding

interface BaseItemLongClickListener<T> {
  fun onLongClick(binding: ViewBinding, positionData: T, position: Int): Boolean
}
