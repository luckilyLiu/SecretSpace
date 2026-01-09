package com.hello.mihe.app.launcher.utils

import android.text.TextUtils
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.hello.mihe.app.launcher.MiheApp
import java.util.UUID

object AdUtil {
    private const val KEY_ANDROID_ID = "key_android_id_adutil"
    var androidIdForApp: String = ""

    init {
        readAndroidIdFromLocal()
    }

    fun checkServices(): Boolean {
        var resultCode = ConnectionResult.SERVICE_MISSING
        val gApi = GoogleApiAvailability.getInstance()
        try {
            resultCode = gApi.isGooglePlayServicesAvailable(MiheApp.app)
        } catch (e: Throwable) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
        return resultCode == ConnectionResult.SUCCESS
    }

    private fun getAdId(): String? {
        return kotlin
            .runCatching { AdvertisingIdClient.getAdvertisingIdInfo(MiheApp.app).id }
            .apply {
                if (this.isFailure) {
                    FirebaseCrashlytics.getInstance().recordException(this.exceptionOrNull()!!)
                }
            }
            .getOrNull()
    }

    private fun readAndroidIdFromLocal() {
        val androidIdTmp = SharedPrefUtils.getStableStringData(MiheApp.app, KEY_ANDROID_ID)
        if (!TextUtils.isEmpty(androidIdTmp) && !androidIdTmp.startsWith("@_fake_")) {
            this.androidIdForApp = androidIdTmp
        }
    }

    fun getAndroidIdSync(): String {
        val androidIdTmp = SharedPrefUtils.getStableStringData(MiheApp.app, KEY_ANDROID_ID)
        if (!TextUtils.isEmpty(androidIdTmp) && !androidIdTmp.startsWith("@_fake_")) {
            androidIdForApp = androidIdTmp
            return androidIdForApp
        }
        var localAndroidID: String? = null
        if (checkServices()) {
            localAndroidID = getAdId()
            if ("00000000-0000-0000-0000-000000000000" == localAndroidID) {
                localAndroidID = ""
            }
        }
        if (localAndroidID != null) {
            SharedPrefUtils.saveStableData(MiheApp.app, KEY_ANDROID_ID, localAndroidID)
        } else if (TextUtils.isEmpty(androidIdTmp)) {
            localAndroidID = "@_fake_${UUID.randomUUID()}"
            SharedPrefUtils.saveStableData(MiheApp.app, KEY_ANDROID_ID, localAndroidID)
            androidIdForApp = localAndroidID
        }
        return androidIdForApp
    }

    fun getAndroidId(listener: ((String) -> Unit)?) {
        val androidIdTmp = SharedPrefUtils.getStableStringData(MiheApp.app, KEY_ANDROID_ID)
        if (!TextUtils.isEmpty(androidIdTmp) && !androidIdTmp.startsWith("@_fake_")) {
            androidIdForApp = androidIdTmp
            listener?.invoke(androidIdForApp)
            return
        }
        Thread {
            var localAndroidID: String? = null
            if (checkServices()) {
                localAndroidID = getAdId()
                if ("00000000-0000-0000-0000-000000000000" == localAndroidID) {
                    localAndroidID = ""
                }
            }
            if (localAndroidID != null) {
                SharedPrefUtils.saveStableData(MiheApp.app, KEY_ANDROID_ID, localAndroidID)
                androidIdForApp = localAndroidID
            } else if (TextUtils.isEmpty(androidIdTmp)) {
                localAndroidID = "@_fake_${UUID.randomUUID()}"
                SharedPrefUtils.saveStableData(MiheApp.app, KEY_ANDROID_ID, localAndroidID)
                androidIdForApp = localAndroidID
            }
            listener?.invoke(androidIdForApp)
        }
            .start()
    }

    fun setAndroidId(androidId: String) {
        this.androidIdForApp = androidId
        SharedPrefUtils.saveStableData(MiheApp.app, KEY_ANDROID_ID, androidIdForApp)
    }

    fun refreshAndroidIdIfNeed() {
        var androidIdTmp = this.androidIdForApp;
        if (!TextUtils.isEmpty(androidIdTmp) && !androidIdTmp.startsWith("@_fake_")) {
            return
        }
        androidIdTmp = SharedPrefUtils.getStableStringData(MiheApp.app, KEY_ANDROID_ID)
        if (!TextUtils.isEmpty(androidIdTmp) && !androidIdTmp.startsWith("@_fake_")) {
            this.androidIdForApp = androidIdTmp
            return
        }
        Thread {
            var localAndroidID: String? = null
            if (checkServices()) {
                localAndroidID = getAdId()
                if ("00000000-0000-0000-0000-000000000000" == localAndroidID) {
                    localAndroidID = ""
                }
            }
            if (localAndroidID != null) {
                SharedPrefUtils.saveStableData(MiheApp.app, KEY_ANDROID_ID, localAndroidID)
                androidIdForApp = localAndroidID
            } else if (TextUtils.isEmpty(androidIdTmp)) {
                localAndroidID = "@_fake_${UUID.randomUUID()}"
                SharedPrefUtils.saveStableData(MiheApp.app, KEY_ANDROID_ID, localAndroidID)
                androidIdForApp = localAndroidID
            }
        }
            .start()
    }
}
