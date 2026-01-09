package com.hello.mihe.app.launcher.ui.act.home

import android.content.Context
import androidx.lifecycle.MutableLiveData
import app.lawnchair.preferences2.PreferenceManager2
import app.lawnchair.util.App
import com.hello.mihe.app.launcher.autotracker.SensorsAnalyticsSdkHelper
import com.hello.mihe.app.launcher.autotracker.TrackUtil
import com.hello.mihe.app.launcher.config.sorter.addHidePackage
import com.hello.mihe.app.launcher.config.sorter.getHidePackageComparator
import com.hello.mihe.app.launcher.config.sorter.removeHidePackage
import com.hello.mihe.app.launcher.ui.base.BaseViewModel
import com.hello.mihe.app.launcher.utils.Vlog
import kotlinx.coroutines.flow.first

class MiheMainActivityViewModel : BaseViewModel() {

    val apps = MutableLiveData<List<App>>()
    fun loadHideApps(context: Context) {
        launchOnUI {
            //
            val preHiddenApps = PreferenceManager2.getInstance(context).preHiddenApps.get().first()
            val hideApps = PreferenceManager2.getInstance(context).hiddenApps.get().first().toMutableSet()
            if (preHiddenApps.isNotEmpty()) {
                hideApps.addAll(preHiddenApps)
                PreferenceManager2.getInstance(context).hiddenApps.set(hideApps)
                val packageNames = ArrayList<String>()
                for (apps in preHiddenApps) {
                    val indexOf = apps.indexOf("/")
                    val packageName = apps.substring(0, indexOf)
                    packageNames.add(packageName)
                    addHidePackage(apps)
                }
                SensorsAnalyticsSdkHelper.getInstance().trackMC("l_e_hide_app_success", TrackUtil.getTrackAppInfo(packageNames))
                PreferenceManager2.getInstance(context).preHiddenApps.set(emptySet())
            }
            val result = loadApp(context).filter {
                hideApps.contains(it.key.toString())
            }.sortedWith(getHidePackageComparator())
            apps.postValue(result)
            Vlog.e("MiheMainActivityViewModel", "apps ${result.size}")
        }
    }

    fun unHideAppByKey(context: Context, key: String) {
        launchOnUI {
            val preferenceManager2 = PreferenceManager2.getInstance(context)
            val hiddenApps = preferenceManager2.hiddenApps.get().first()
            val newSet = hiddenApps.toMutableSet()
            newSet.remove(key)
            removeHidePackage(key)
            preferenceManager2.hiddenApps.set(value = newSet)
        }
    }


}
