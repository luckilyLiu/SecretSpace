package com.hello.mihe.app.launcher.ui.act.login

import android.app.Activity
import android.content.Context
import android.text.method.LinkMovementMethod
import android.widget.Button
import android.widget.TextView
import com.android.launcher3.R
import com.hello.mihe.app.launcher.constant.URL_APP_AGREEMENT
import com.hello.mihe.app.launcher.constant.URL_APP_PRIVACY
import com.hello.mihe.app.launcher.utils.StringUtils
import com.hello.sandbox.common.util.ViewUtil
import com.lxj.xpopup.core.CenterPopupView

class PrivacyPolicyPopup(context: Context) : CenterPopupView(context) {

    override fun getImplLayoutId(): Int {
        return R.layout.mihe_privacy_policy_content
    }

    lateinit var textView: TextView

    var afterAgree: Runnable? = null
    var disAgree: Runnable? = null

    override fun onCreate() {
        super.onCreate()
        val userAgreement: String = context.getString(R.string.PRIVACY_POLICY_DLG_USER_AGREEMENT)
        val privacyPolicy: String = context.getString(R.string.PRIVACY_POLICY_DLG_PRIVACY_POLICY)
        textView = findViewById(R.id.tv_user_agreement_privacy_policy)
        textView.text =
            StringUtils.getLinkSubstringWithColorToWebView(
                context as Activity?,
                context.resources.getColor(R.color.mihe_button_bg),
                String.format(
                    context.getString(R.string.PRIVACY_POLICY_DLG_FIRST_CONTENT_WITH_LINK),
                    userAgreement,
                    privacyPolicy
                ),
                userAgreement,
                URL_APP_AGREEMENT,
                privacyPolicy,
                URL_APP_PRIVACY
            )
        textView.setMovementMethod(LinkMovementMethod.getInstance())
        val agreeButton = findViewById<Button>(R.id.btn_agree)
        ViewUtil.singleClickListener(agreeButton) { afterAgree!!.run() }
        val disAgreeButton = findViewById<TextView>(R.id.tv_disagree)
        ViewUtil.singleClickListener(disAgreeButton) { disAgree!!.run() }
    }
}
