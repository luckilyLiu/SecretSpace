package com.hello.mihe.app.launcher.ui.act.hideapp

import app.lawnchair.util.App
import com.hello.sandbox.common.util.HanziToPinyin

class AppExt(
    val app: App,
    val firstLetter: String =
        if (app.label.isEmpty()) "" else HanziToPinyin.getSortLetter(app.label).first,
    var isSelected: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as AppExt
        return app == other.app
    }

    override fun hashCode() = app.key.hashCode()
}