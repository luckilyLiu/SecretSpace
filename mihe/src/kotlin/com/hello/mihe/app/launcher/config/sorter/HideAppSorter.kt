package com.hello.mihe.app.launcher.config.sorter

import android.content.Context
import app.lawnchair.util.App
import com.hello.mihe.app.launcher.MiheApp
import com.hello.mihe.app.launcher.api.Api
import com.hello.mihe.app.launcher.api.Api.listAdapter
import java.io.File


private fun getSortFile(): File {
    val dir = MiheApp.app.getDir("mihe", Context.MODE_PRIVATE)
    return File(dir, "hide_app_sort.json")
}


private val sortList = mutableListOf<String>()


fun getHidePackageComparator(): Comparator<App> {
    ensureSortList()
    return object : Comparator<App> {
        override fun compare(o1: App?, o2: App?): Int {
            if (o1 == null || o2 == null) {
                return 0
            }

            val first = sortList.indexOf(o1.key.toString())
            val second = sortList.indexOf(o2.key.toString())
            return second.compareTo(first)
        }
    }
}

private fun ensureSortList() {
    if (sortList.isEmpty()) {
        val sortFile = getSortFile()
        if (sortFile.exists()) {
            val tmp = runCatching {
                val jsonTxt = sortFile.readText()
                Api.moshi.listAdapter<String>().fromJson(jsonTxt)
            }
            if (tmp.isSuccess) {
                sortList.addAll(tmp.getOrNull()!!)
            }
        }
    }
}

private fun writeToDesk() {
    runCatching {
        val jsonTxt = Api.moshi.listAdapter<String>().toJson(sortList)
        getSortFile().writeText(jsonTxt)
    }
}

fun addHidePackage(packageName: String) {
    ensureSortList()
    if (!sortList.contains(packageName)) {
        sortList.add(packageName)
        writeToDesk()
    }
}

fun removeHidePackage(packageName: String) {
    ensureSortList()
    if (sortList.remove(packageName)) {
        writeToDesk()
    }
}