package com.hello.mihe.app.launcher.view.adapter

import androidx.viewbinding.ViewBinding

interface BaseItemClickListener<T> {
  fun onClick(binding: BaseHolder<T, ViewBinding, BaseAdapter<T>>, positionData: T, position: Int)
}
