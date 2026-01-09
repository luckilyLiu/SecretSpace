package com.hello.mihe.app.launcher.config

import com.hello.mihe.app.launcher.MiheApp
import com.hello.mihe.app.launcher.api.Api
import com.hello.mihe.app.launcher.api.models.AppGlobalConfigResponse
import com.hello.mihe.app.launcher.api.models.RemoteConfigRequest
import com.hello.mihe.app.launcher.api.models.RootJson
import com.hello.mihe.app.launcher.utils.EncryptUtil
import com.hello.mihe.app.launcher.utils.SharedPrefUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object RemoteConfig {
    private var lastRefreshIpTime = 0L
    fun generateRemoteConfigRequestSign(timeStamp: Long, version: String): String {
        return EncryptUtil.getSHA256(Api.appID + Api.appKey + "android" + timeStamp + version)
    }

    fun getAppGlobalConfig() {
        val timeStamp = System.currentTimeMillis() / 1000
        if (timeStamp - lastRefreshIpTime < 5 * 60) {
            return
        }

        val version = "laucher_global_config"
        val request = RemoteConfigRequest(
            Api.appID, version, timeStamp, generateRemoteConfigRequestSign(timeStamp, version)
        )
        Api.httpService.getAppGlobalConfig(request).enqueue(object : Callback<RootJson<AppGlobalConfigResponse>> {
            override fun onResponse(p0: Call<RootJson<AppGlobalConfigResponse>>, p1: Response<RootJson<AppGlobalConfigResponse>>) {
                if (p1.isSuccessful) {
                    kotlin.runCatching {
                        val result = p1.body()
                        if (result != null && result.meta?.code == 200 && result.data != null) {
                            lastRefreshIpTime = timeStamp
                            SharedPrefUtils.saveStableData(MiheApp.app, "feed_back_popup", result.data.feedBackPopupEnable)
                        }
                    }
                }
            }
            override fun onFailure(p0: Call<RootJson<AppGlobalConfigResponse>>, p1: Throwable) {
            }
        })
    }

    fun isFeedBackPopupEnable() =
        SharedPrefUtils.getStableBooleanData(MiheApp.app, "feed_back_popup", true)
}