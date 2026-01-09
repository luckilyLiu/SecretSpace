package com.hello.mihe.app.launcher.utils

import app.lawnchair.LawnchairApp


object EnvUtil {
    private const val IS_STAGING = "isStaging"
    var isStaging = getEnv()

    /** 之后可以提供切换staging环境的选项 */
    fun switchEnv(switch: Boolean) {
        isStaging = switch
        SharedPrefUtils.saveData(LawnchairApp.instance, IS_STAGING, switch)
    }

    fun getEnv(): Boolean {
        return SharedPrefUtils.getBooleanWithDefault(LawnchairApp.instance, IS_STAGING, false)
    }
}
