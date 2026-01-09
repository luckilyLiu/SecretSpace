package com.hello.mihe.app.launcher.ui.act.home

import app.lawnchair.util.App
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MiheMainAppInfo(
    val app: App? = null,
    val drawableId: Int = 0,
    val itemNameId: Int = 0
) {
    fun isEmptyItem() = app == null && drawableId == -1 && itemNameId == -1
}

val EmptyAppInfo = MiheMainAppInfo(drawableId = -1, itemNameId = -1)