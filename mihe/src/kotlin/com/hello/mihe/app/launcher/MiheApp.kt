package com.hello.mihe.app.launcher

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Process
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import app.lawnchair.LawnchairLauncher
import com.android.launcher3.BuildConfig
import com.android.launcher3.util.ActivityLifecycleCallbacksAdapter
import com.google.firebase.FirebaseApp
import com.hello.mihe.app.launcher.autotracker.SensorsAnalyticsSdkHelper
import com.hello.mihe.app.launcher.config.RemoteConfig
import com.hello.mihe.app.launcher.ui.act.home.SwitchLauncherDialogActivity
import com.hello.mihe.app.launcher.utils.AdUtil
import com.hello.mihe.app.launcher.utils.Vlog
import com.hello.mihe.app.launcher.view.imageloader.initCoil
import com.hello.sandbox.common.util.UtilSDk


object MiheApp {
    lateinit var app: Application
    fun onCreate(application: Application) {
        UtilSDk.init(
            application,
            BuildConfig.DEBUG, BuildConfig.DEBUG,
        )
        initCoil(application)
        Vlog.enableLog = BuildConfig.DEBUG
        app = application
        AdUtil.getAndroidId {
            SensorsAnalyticsSdkHelper.getInstance().init()
        }
        application.registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacksAdapter {
            val activities = mutableListOf<Activity>()
            override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
                super.onActivityCreated(activity, bundle)
                if (activity is LawnchairLauncher) {
                    activities.forEach {
                        if (it is SwitchLauncherDialogActivity) {
                            it.finishAndRemoveTask()
                        } else {
                            it.finish()
                        }
                    }
                }
                activities.add(activity)
            }

            override fun onActivityDestroyed(activity: Activity) {
                super.onActivityDestroyed(activity)
                activities.remove(activity)
            }
        })
        initCrashConfig(application)
        RemoteConfig.getAppGlobalConfig()
    }

    private fun initCrashConfig(application: Application) {
        val processName = getProcessName(application)
        if (processName.isNullOrEmpty() && application.packageName != processName) {
            if (FirebaseApp.initializeApp(application) == null) {
                android.util.Log.i("FirebaseApp", "FirebaseApp initialization unsuccessful")
            } else {
                android.util.Log.i("FirebaseApp", "FirebaseApp initialization successful")
            }
        }
    }
}

private fun getProcessName(context: Context): String? {
    return runCatching {
        val pid = Process.myPid()
        var processName: String? = null
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (info in am.runningAppProcesses) {
            if (info.pid == pid) {
                processName = info.processName
                break
            }
        }
        processName
    }.getOrNull()
}

inline fun View.singleClickListener(crossinline onClickBlock: (v: View) -> Unit) {
    this.setOnClickListener(
        object : OnClickListener {
            var lastClick: Long = 0
            override fun onClick(v: View) {
                if (SystemClock.uptimeMillis() - lastClick > 500) {
                    lastClick = SystemClock.uptimeMillis()
                    onClickBlock(v)
                }
            }
        },
    )
}


fun View.isTouchOn(ev: MotionEvent): Boolean {
    val outPint = intArrayOf(0, 0)
    this.getLocationOnScreen(outPint)
    return outPint[0] < ev.rawX &&
            ev.rawX < outPint[0] + this.width &&
            outPint[1] < ev.rawY &&
            ev.rawY < outPint[1] + this.height
}