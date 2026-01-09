package com.hello.mihe.app.launcher.autotracker

import android.text.TextUtils
import android.util.Log
import com.android.launcher3.BuildConfig
import com.hello.mihe.app.launcher.MiheApp
import com.hello.mihe.app.launcher.utils.AbiUtils.getSupportAbi
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File


object TrackUtil {
    val trackScope = CoroutineScope(Dispatchers.IO + CoroutineName("track"))
    inline fun track(crossinline block:suspend () -> Unit) {
        trackScope.launch {
            try {
                block()
            } catch (th: Throwable) {
                if (BuildConfig.DEBUG) {
                    th.printStackTrace()
                }
            }
        }
    }


    fun getTrackAppInfo(packageName: String): JSONObject {
        val result = JSONObject()
        kotlin.runCatching {
            val packageInfo =
                MiheApp.app.packageManager.getPackageInfo(packageName, 0) ?: return result
            if (packageInfo.applicationInfo == null) return result
            val sourceDir = packageInfo.applicationInfo!!.sourceDir
            val appName =
                packageInfo.applicationInfo!!.loadLabel(MiheApp.app.packageManager).toString()
            val versionName = packageInfo.versionName
            result.put("app_name", appName)
            result.put("app_package", packageInfo.applicationInfo!!.packageName)
            result.put("app_version", versionName)
            if (File(sourceDir).exists()) {
                val file = File(sourceDir)
                result.put("app_abi", getSupportAbi(file))
            } else {
                result.put("app_abi", "unknown")
            }
        }
        return result
    }

    fun getTrackAppInfo(data: List<String>): JSONObject {
        val result = JSONObject()
        var appNames = StringBuilder()
        var packageNames = StringBuilder()
        var versionNames = StringBuilder()
        var appAbis = StringBuilder()
        for (packageName in data) {
            kotlin.runCatching {
                val packageInfo =
                    MiheApp.app.packageManager.getPackageInfo(packageName, 0) ?: return result
                if (packageInfo.applicationInfo == null) return result
                val sourceDir = packageInfo.applicationInfo!!.sourceDir
                val appName =
                    packageInfo.applicationInfo!!.loadLabel(MiheApp.app.packageManager).toString()
                val versionName = packageInfo.versionName
                val packageName = packageInfo.applicationInfo!!.packageName
                var appAbi = ""
                if (File(sourceDir).exists()) {
                    val file = File(sourceDir)
                    var supportAbi = getSupportAbi(file)
                    if (TextUtils.isEmpty(supportAbi)) {
                        supportAbi = "unknown"
                    }
                    appAbi = supportAbi
                } else {
                    appAbi = "unknown"
                }
                if (TextUtils.isEmpty(appNames)) {
                    appNames.append(appName)
                } else {
                    appNames.append(",$appName")
                }
                if (TextUtils.isEmpty(appAbis)) {
                    appAbis.append(appAbi)
                } else {
                    appAbis.append(",$appAbi")
                }
                if (TextUtils.isEmpty(versionNames)) {
                    versionNames.append(versionName)
                } else {
                    versionNames.append(",$versionName")
                }
                if (TextUtils.isEmpty(packageNames)) {
                    packageNames.append(packageName)
                } else {
                    packageNames.append(",$packageName")
                }
            }
        }
        result.put("app_name", appNames)
        result.put("app_package", packageNames)
        result.put("app_version", versionNames)
        result.put("app_abi", appAbis)
        return result
    }
}
