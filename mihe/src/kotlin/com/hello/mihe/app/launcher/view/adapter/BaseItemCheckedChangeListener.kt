package com.hello.mihe.app.launcher.view.adapter

interface BaseItemCheckedChangeListener<T> {
  fun onCheckedChanged(positionData: T, position: Int, isChecked: Boolean)
}
