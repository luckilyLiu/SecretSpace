package com.hello.mihe.app.launcher.api.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
class IpRecordRequest(
    @Json(name = "app_id")
    val appId: String,
    @Json(name = "gaid")
    val gaid: String,
    @Json(name = "guest_id")
    val guestId: String,
    @Json(name = "id")
    val id: String,
    @Json(name = "sign")
    val sign: String,
    @Json(name = "timestamp")
    val timestamp: Long,
    @Json(name = "user_id")
    val userId: Long
)

@JsonClass(generateAdapter = true)
data class IpRecordResponse(
    @Json(name = "gaid")
    val gaid: String?,
    @Json(name = "guest_id")
    val guest_id: String?,
    @Json(name = "ip")
    val ip: String?,
    @Json(name = "country_en")
    val country_en: String?,
    @Json(name = "country_zh")
    val country_zh: String?,
    @Json(name = "city")
    val city: String?,
    @Json(name = "user_id")
    val user_id: String?
)
