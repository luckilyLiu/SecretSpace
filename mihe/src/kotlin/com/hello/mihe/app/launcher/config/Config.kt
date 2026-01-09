package com.hello.mihe.app.launcher.config

import app.lawnchair.LawnchairApp
import com.hello.mihe.app.launcher.utils.SharedPrefUtils
import com.hello.mihe.app.launcher.utils.TimeHelper


fun needShowHideAppSuccessTip() =
    SharedPrefUtils.getBooleanWithDefault(LawnchairApp.instance, "need_show_hide_app_success_tip", true)

fun saveNeedShowHideAppSuccessTip() {
    SharedPrefUtils.saveData(LawnchairApp.instance, "need_show_hide_app_success_tip", false)
}

fun needShowHideAppSuccessPopup() =
    SharedPrefUtils.getBooleanWithDefault(LawnchairApp.instance, "need_show_hide_app_success_tip_popup", true)

fun saveNeedShowHideAppSuccessPopup() {
    SharedPrefUtils.saveData(LawnchairApp.instance, "need_show_hide_app_success_tip_popup", false)
}

fun needShowPrivacyPolicy() = SharedPrefUtils.getBooleanWithDefault(LawnchairApp.instance, "key_need_show_privacy_policy", true)
fun saveShowPrivacyPolicyState() {
    SharedPrefUtils.saveData(LawnchairApp.instance, "key_need_show_privacy_policy", false)
}


fun needShowUnHideTip() = SharedPrefUtils.getBooleanWithDefault(LawnchairApp.instance, "key_need_show_un_hide_tip", true)
fun saveShowUnHideTipState() {
    SharedPrefUtils.saveData(LawnchairApp.instance, "key_need_show_un_hide_tip", false)
}


fun needShowFeedBackPopup(): Boolean {
    if (!RemoteConfig.isFeedBackPopupEnable()) return false
    val showCount = SharedPrefUtils.getIntData(LawnchairApp.instance, "key_show_feed_back_popup_count")
    if (showCount >= 3) return false
    val lastShowTime = SharedPrefUtils.getLongData(LawnchairApp.instance, "key_show_feed_back_popup_show_time")
    val currentTimeMillis = System.currentTimeMillis()
    if (lastShowTime != 0L) {
        if (TimeHelper.isSameDay(currentTimeMillis, lastShowTime)) {
            return false
        }
    }
    return true
}

fun saveNeedShowFeedBackPopupState(count: Int = -1) {
    var showCount = count;
    if (showCount == -1) {
        showCount = SharedPrefUtils.getIntData(LawnchairApp.instance, "key_show_feed_back_popup_count") + 1
    }
    SharedPrefUtils.saveData(LawnchairApp.instance, "key_show_feed_back_popup_count", showCount)
    SharedPrefUtils.saveData(LawnchairApp.instance, "key_show_feed_back_popup_show_time", System.currentTimeMillis())
}