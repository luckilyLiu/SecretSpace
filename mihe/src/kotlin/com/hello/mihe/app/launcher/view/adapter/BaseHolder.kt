package com.hello.mihe.app.launcher.view.adapter

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewbinding.ViewBinding

abstract class BaseHolder<T, out VB : ViewBinding, out BA : BaseAdapter<T>>(
  val binding: VB,
  val adapter: BA
) : ViewHolder(binding.root) {
  abstract fun bindView(positionData: T, position: Int)
}
