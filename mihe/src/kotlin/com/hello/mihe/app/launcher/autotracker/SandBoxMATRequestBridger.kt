package com.hello.mihe.app.launcher.autotracker

import com.hello.mihe.app.launcher.api.Api
import com.hello.mihe.app.launcher.api.models.IpRecordRequest
import com.hello.mihe.app.launcher.api.models.IpRecordResponse
import com.hello.mihe.app.launcher.api.models.RootJson
import com.hello.mihe.app.launcher.config.RemoteConfig
import com.hello.mihe.app.launcher.config.UserManager
import com.hello.mihe.app.launcher.constant.ELEMENT_URL
import com.hello.mihe.app.launcher.constant.REGISTRATION_URL
import com.hello.mihe.app.launcher.constant.SAVE_SCHEMA_URL
import com.hello.mihe.app.launcher.constant.SAVE_URL
import com.hello.mihe.app.launcher.network.HttpUtil
import com.hello.mihe.app.launcher.utils.AdUtil
import com.hello.mihe.app.launcher.utils.EncryptUtil
import com.hello.mihe.app.launcher.utils.Network
import com.immomo.autotracker.android.sdk.bridge.MATRequestBridger
import com.immomo.autotracker.android.sdk.util.MATHttpUtil
import com.immomo.autotracker.android.sdk.util.ThreadUtils
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SandBoxMATRequestBridger : MATRequestBridger {

    override fun saveUrl(): String {
        return SAVE_URL
    }

    override fun saveSchemaUrl(): String {
        return SAVE_SCHEMA_URL
    }

    override fun getElementUrl(): String {
        return ELEMENT_URL
    }

    override fun validateRegistrationUrl(): String {
        return REGISTRATION_URL
    }

    override fun post(
        url: String,
        params: MutableMap<String, String>?,
        callBack: MATHttpUtil.AutoTrackHttpCallBack?
    ) {
        ThreadUtils.getSinglePool().execute {
            var json = JSONObject()
            params?.forEach { run { json.put(it.key, it.value) } }
            try {
                var result = HttpUtil.post(Network.getOkHttpClient(), "https://$url", json, null)
                callBack?.onSuccess(result)
            } catch (ignored: Throwable) {
                callBack?.onFail(ignored.message)
            }
            refreshLocalConfig()
        }
    }

    override fun post(url: String, params: MutableMap<String, String>?): String {
        var json = JSONObject()
        params?.forEach { run { json.put(it.key, it.value) } }
        refreshLocalConfig()
        return HttpUtil.post(Network.getOkHttpClient(), "https://$url", json, null)
    }
}

fun refreshLocalConfig() {
    refreshIp()
    AdUtil.refreshAndroidIdIfNeed()
    RemoteConfig.getAppGlobalConfig()
}

private var lastRefreshIpTime = 0L
private fun refreshIp() {
    val timestamp = System.currentTimeMillis() / 1000L
    if (timestamp - lastRefreshIpTime < 5 * 60 && UserManager.ip.isEmpty()) {
        return
    }
    lastRefreshIpTime = timestamp
    val gaid = AdUtil.androidIdForApp
    val userId = UserManager.userId
    val guestId = UserManager.guestId
    val sign = EncryptUtil.getSHA256(Api.appID + Api.appKey + gaid + guestId + guestId + timestamp + userId)
    val request = IpRecordRequest(
        Api.appID, gaid, guestId, guestId, sign, timestamp, userId,
    )
    Api.httpService.ipRecord(request).enqueue(object : Callback<RootJson<IpRecordResponse>> {
        override fun onResponse(p0: Call<RootJson<IpRecordResponse>>, p1: Response<RootJson<IpRecordResponse>>) {
            if (p1.isSuccessful) {
                runCatching {
                    val body = p1.body()
                    if (body?.data != null) {
                        UserManager.saveIpInfo(body.data)
                    }
                }
            }
        }

        override fun onFailure(p0: Call<RootJson<IpRecordResponse>>, p1: Throwable) {
        }

    })
}