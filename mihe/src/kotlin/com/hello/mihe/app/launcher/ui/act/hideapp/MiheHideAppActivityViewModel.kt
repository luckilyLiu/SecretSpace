package com.hello.mihe.app.launcher.ui.act.hideapp

import android.content.Context
import android.text.Editable
import androidx.lifecycle.MutableLiveData
import app.lawnchair.preferences2.PreferenceManager2
import com.hello.mihe.app.launcher.config.sorter.addHidePackage
import com.hello.mihe.app.launcher.ui.base.BaseViewModel
import com.hello.mihe.app.launcher.utils.SortComparator
import com.hello.mihe.app.launcher.utils.StringUtils
import com.hello.sandbox.common.util.HanziToPinyin
import kotlinx.coroutines.flow.first

private val DEFAULT_PACKAGE = listOf(
    "com.facebook.katana",
    "com.whatsapp",
    "com.twitter.android",
    "com.ss.android.ugc.trill",
    "com.instagram.android",
    "com.tinder",
    "org.telegram.messenger",
    "com.google.android.youtube",
)

class MiheHideAppActivityViewModel : BaseViewModel() {
    val systemApps = MutableLiveData<List<AppExt>>()
    val suggestApps = MutableLiveData<List<AppExt>>()
    val filterApp = MutableLiveData<List<AppExt>>()
    val hideApp = MutableLiveData<Boolean>()
    private var firstLoadData = true
    fun loadApps(context: Context) {
        launchOnUI {
            val preferenceManager2 = PreferenceManager2.getInstance(context)
            val hiddenApps = preferenceManager2.hiddenApps.get().first()
            val apps = loadApp(context).filter {
                !hiddenApps.contains(it.key.toString()) && it.key.componentName.packageName != context.packageName
            }.sortedWith(
                SortComparator()
            ).map { AppExt(it) }
            val suggest = mutableListOf<AppExt>()
            DEFAULT_PACKAGE.forEach {
                for (appInfo in apps) {
                    if (it == appInfo.app.key.componentName.packageName) {
                        suggest.add(appInfo)
                    }
                }
            }
            if (firstLoadData) {
                firstLoadData = false
                if (suggest.isNotEmpty()) {
                    suggest[0].isSelected = true
                }
            }
            suggestApps.postValue(suggest)
            systemApps.postValue(apps)
        }
    }

    fun filterApp(newTextEditable: Editable?, allSystemInstallAppInfos: List<AppExt>) {
        launchOnUI {
            val newText = newTextEditable?.toString()
            val filter = if (newText.isNullOrEmpty()) {
                allSystemInstallAppInfos
            } else
                if (newText.length == 1 && StringUtils.isLetter(newText)) {
                    allSystemInstallAppInfos.filter { it.firstLetter.equals(newText, true) }
                } else {
                    allSystemInstallAppInfos.filter {
                        it.app.label.contains(newText, true) or
                                HanziToPinyin.getInstance().getSpelling(it.app.label).contains(newText, true)
                    }
                }
            filterApp.postValue(filter)
        }
    }

    fun hideApp(context: Context, adapter: SystemInstallAppAdapter) {
        launchOnUI {
            val preferenceManager2 = PreferenceManager2.getInstance(context)
            val hiddenApps = preferenceManager2.hiddenApps.get().first()
            val newSet = hiddenApps.toMutableSet()
            adapter.getData().forEach {
                if (it.isSelected) {
                    val key = it.app.key.toString()
                    newSet.add(key)
                    addHidePackage(key)
                }
            }
            preferenceManager2.hiddenApps.set(value = newSet)
            hideApp.postValue(true)
        }
    }

    fun perHideApp(context: Context, adapter: SystemInstallAppAdapter) {
        launchOnUI {
            val preferenceManager2 = PreferenceManager2.getInstance(context)
            val hiddenApps = preferenceManager2.preHiddenApps.get().first()
            val newSet = hiddenApps.toMutableSet()
            adapter.getData().forEach {
                if (it.isSelected) {
                    newSet.add(it.app.key.toString())
                }
            }
            if (newSet.isNotEmpty())
                preferenceManager2.preHiddenApps.set(value = newSet)
        }
    }

}
