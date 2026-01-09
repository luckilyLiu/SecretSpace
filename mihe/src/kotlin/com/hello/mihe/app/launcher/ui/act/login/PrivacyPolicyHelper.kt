package com.hello.mihe.app.launcher.ui.act.login

import android.content.Context
import android.graphics.Color
import app.lawnchair.LawnchairApp
import com.android.launcher3.R
import com.hello.mihe.app.launcher.utils.SharedPrefUtils
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.util.XPopupUtils

object PrivacyPolicyHelper {

    private const val KEY = "has_show_privacy_policy_dlg"

    fun isNeedShowPrivacyPolicyDlg(context: Context): Boolean {
        return SharedPrefUtils.getBooleanWithDefault(context, KEY, true)
    }

    fun updateShowPrivacyPolicy(context: Context, needShow: Boolean) {
        SharedPrefUtils.saveData(context, KEY, needShow)
    }

    fun showPrivacyPop(context: Context, confirm: Runnable, cancel: Runnable) {
        val privacyPolicyPopup = PrivacyPolicyPopup(context)
        XPopup.Builder(context)
            .moveUpToKeyboard(false)
            .maxWidth(XPopupUtils.getScreenWidth(context) - XPopupUtils.dp2px(context, 40f))
            .isDestroyOnDismiss(true)
            .dismissOnTouchOutside(false)
            .dismissOnBackPressed(false)
            .statusBarBgColor(Color.TRANSPARENT)
            .shadowBgColor(context.getColor(R.color.app_theme_color))
            .navigationBarColor(Color.TRANSPARENT)
            .asCustom(this.let { privacyPolicyPopup })
            .show()
        privacyPolicyPopup.afterAgree = Runnable {
            updateShowPrivacyPolicy(LawnchairApp.instance, false)
            confirm.run()
        }
        privacyPolicyPopup.disAgree = Runnable {
            cancel.run()
//            privacyPolicyPopup.dismiss()
        }
    }
}
