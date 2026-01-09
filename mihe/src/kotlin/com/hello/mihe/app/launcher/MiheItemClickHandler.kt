package com.hello.mihe.app.launcher

import com.android.launcher3.model.data.ItemInfo
import com.hello.mihe.app.launcher.autotracker.SensorsAnalyticsSdkHelper

object MiheItemClickHandler {
    fun startAppShortcutOrInfoActivity(item: ItemInfo) {
        if (item.componentKey?.componentName?.packageName == MiheApp.app.packageName) {
            SensorsAnalyticsSdkHelper.getInstance().trackMC("l_e_app_launcher_click")
        }
    }
}