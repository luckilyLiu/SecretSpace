package com.hello.mihe.app.launcher.ui.act.login

import android.content.Intent
import android.os.Bundle
import com.android.launcher3.R
import com.hello.mihe.app.launcher.autotracker.SensorsAnalyticsSdkHelper
import com.hello.mihe.app.launcher.autotracker.TrackUtil
import com.hello.mihe.app.launcher.config.UserManager
import com.hello.mihe.app.launcher.config.needShowPrivacyPolicy
import com.hello.mihe.app.launcher.config.saveShowPrivacyPolicyState
import com.hello.mihe.app.launcher.ui.act.home.MiheMainActivity
import com.hello.mihe.app.launcher.ui.base.BaseActivity
import kotlinx.coroutines.delay

class LoginActivity : BaseActivity() {
    private val viewModel by lazy { createViewModel(LoginActivityViewModel::class.java) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!needShowPrivacyPolicy()) {
            startActivity(Intent(this, MiheMainActivity::class.java))
            finish()
            return
        }
        setContentView(R.layout.mihe_activity_login)
        showPrivacyDlg()
        viewModel.registerUserObserver.observe(this) {
            if (it.isSuccess()) {
                saveShowPrivacyPolicyState()
                UserManager.login(it.data!!)
                SensorsAnalyticsSdkHelper.getInstance().trackMC("l_e_signup_success")
                startActivity(Intent(this, MiheMainActivity::class.java))
                finish()
            }
        }
    }


    private fun showPrivacyDlg() {
        TrackUtil.track {
            delay(1000)
            SensorsAnalyticsSdkHelper.getInstance().trackMV("l_e_signup_popup")
        }
        PrivacyPolicyHelper.showPrivacyPop(
            this,
            {
                SensorsAnalyticsSdkHelper.getInstance().trackMC("l_e_signup_confirm")
                viewModel.login()
            },
            {
                SensorsAnalyticsSdkHelper.getInstance().trackMC("l_e_signup_cancel")
                moveTaskToBack(true)
            },
        )
    }

//    private fun showPrompt() {
//        val prompt =
//            BasePopup(
//                this,
//                getString(R.string.prompt_popup_title),
//                getString(R.string.prompt_popup_description),
//                { showPrivacyDlg() },
//                { finish() },
//                getString(R.string.prompt_popup_confirm),
//                getString(R.string.prompt_popup_cancel),
//                false,
//            )
//        XPopup.Builder(this)
//            .moveUpToKeyboard(false)
//            .maxWidth(XPopupUtils.getScreenWidth(this) - XPopupUtils.dp2px(this, 40f))
//            .isDestroyOnDismiss(true)
//            .dismissOnTouchOutside(false)
//            .dismissOnBackPressed(false)
//            .asCustom(this.let { prompt })
//            .show()
//    }
}


