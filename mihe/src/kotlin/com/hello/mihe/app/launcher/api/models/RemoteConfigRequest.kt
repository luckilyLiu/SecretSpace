package com.hello.mihe.app.launcher.api.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class RemoteConfigRequest(

    @Json(name = "app_id")
    var appId: String,
    @Json(name = "version")
    var version: String,
    @Json(name = "timestamp")
    var timestamp: Long,
    @Json(name = "sign")
    var sign: String,
    @Json(name = "os")
    var os: String = "android"
)

@JsonClass(generateAdapter = true)
class FeedbackDefaultReasonResponse(
    @Json(name = "cn")
    val cn: List<String>,
    @Json(name = "en")
    val en: List<String>,
)

@JsonClass(generateAdapter = true)
class AppGlobalConfigResponse(
    @Json(name = "feed_back_popup")
    val feedBackPopupEnable: Boolean = false)
