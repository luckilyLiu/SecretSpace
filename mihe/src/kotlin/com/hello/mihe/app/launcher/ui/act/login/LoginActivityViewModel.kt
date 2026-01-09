package com.hello.mihe.app.launcher.ui.act.login

import android.os.Build
import com.hello.mihe.app.launcher.api.Api
import com.hello.mihe.app.launcher.api.Api.appID
import com.hello.mihe.app.launcher.api.Api.appKey
import com.hello.mihe.app.launcher.api.models.Detail
import com.hello.mihe.app.launcher.api.models.LoginRequest
import com.hello.mihe.app.launcher.ui.base.BaseViewModel
import com.hello.mihe.app.launcher.ui.base.StateLiveData
import com.hello.mihe.app.launcher.utils.AdUtil
import com.hello.mihe.app.launcher.utils.EncryptUtil
import java.util.Locale

class LoginActivityViewModel : BaseViewModel() {
    val registerUserObserver = StateLiveData<Long>()
    fun login() {
        launchOnUI {
            val response = httpCall {
                var gaid = AdUtil.androidIdForApp
                if (gaid.isEmpty()) {
                    gaid = AdUtil.getAndroidIdSync()
                }
                val timestamp = System.currentTimeMillis() / 1000L
                val locale = Locale.getDefault()
                val detail = Detail(
                    gaid, locale.country, locale.language, Build.MODEL, Build.SUPPORTED_ABIS.joinToString(), Build.BRAND
                )
                val sign = EncryptUtil.getSHA256(
                    appID + appKey + detail.country + gaid + gaid + detail.language + detail.phone_abi + detail.phone_brand + detail.phone_model +
                            timestamp
                )
                val loginRequest = LoginRequest(
                    gaid, Api.appID, timestamp, sign, detail

                )
                Api.httpService.activateUser(loginRequest)
            }
            registerUserObserver.postValue(response)
        }
    }
}