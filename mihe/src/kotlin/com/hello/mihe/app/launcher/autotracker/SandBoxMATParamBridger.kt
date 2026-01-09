package com.hello.mihe.app.launcher.autotracker

import android.os.Build
import com.android.launcher3.BuildConfig
import com.hello.mihe.app.launcher.config.UserManager
import com.hello.mihe.app.launcher.utils.AdUtil
import com.immomo.autotracker.android.sdk.bridge.MATParamBridger

class SandBoxMATParamBridger : MATParamBridger {
    override fun getUserId(): String {
        // 这里用deviceId，因为userid可能生成的有重复的
        return UserManager.userId.toString()
    }

    override fun getGuestId(): String {
        return UserManager.userId.toString()
    }

    override fun getLng(): String {
        return "-1"
    }

    override fun getLat(): String {
        return "-1"
    }

    override fun getIp(): String {
        return UserManager.ip
    }

    override fun getAppVersion(): String {
        return BuildConfig.VERSION_NAME
    }

    override fun getAppBuildVersion(): String {
        return BuildConfig.VERSION_DISPLAY_NAME
    }

    override fun getPlatformVersion(): String {
        return "Android " + Build.VERSION.SDK_INT
    }

    override fun getDeviceBranch(): String {
        return Build.BRAND
    }

    override fun getDeviceModel(): String {
        return Build.MODEL
    }

    override fun getOAID(): String {
        return AdUtil.androidIdForApp
    }

    override fun getDeviceId(): String {
        return AdUtil.androidIdForApp
    }

    override fun getAbTestId(): String {
        return "-1"
    }
}
