package com.hello.mihe.app.launcher.ui.base.owner

import androidx.appcompat.app.AppCompatActivity

interface FragmentOwner {
    fun hostActivity(): AppCompatActivity
    fun showLoading()
    fun showLoading(describe: String)
    fun hideLoading()
}
