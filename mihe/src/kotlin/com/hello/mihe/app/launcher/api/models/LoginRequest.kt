package com.hello.mihe.app.launcher.api.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class LoginRequest(
    @Json(name = "id")
    val id: String,
    @Json(name = "app_id")
    val app_id: String,
    @Json(name = "timestamp")
    val timestamp: Long,
    @Json(name = "sign")
    val sign: String,
    @Json(name = "detail")
    val detail: Detail
)

@JsonClass(generateAdapter = true)
data class Detail(
    @Json(name = "gaid")
    val gaid: String,
    @Json(name = "country")
    val country: String,
    @Json(name = "language")
    val language: String,
    @Json(name = "phone_brand")
    val phone_brand: String,
    @Json(name = "phone_model")
    val phone_model: String,
    @Json(name = "phone_abi")
    val phone_abi: String
)