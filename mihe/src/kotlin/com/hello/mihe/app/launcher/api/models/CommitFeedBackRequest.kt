package com.hello.mihe.app.launcher.api.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CommitFeedBackRequest(
    @Json(name = "id") val id: String,
    @Json(name = "app_id") val appId: String,
    @Json(name = "timestamp") val timestamp: Long,
    @Json(name = "sign") val sign: String,
    @Json(name = "gaid") val gaid: String,
    @Json(name = "grade") val grade: String,
    @Json(name = "selected") val selected: String,
    @Json(name = "discuss") val discuss: String,
    @Json(name = "language") val language: String,
)