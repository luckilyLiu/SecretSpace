package com.hello.mihe.app.launcher.ui.act.login

import android.content.Intent
import android.os.Bundle
import com.hello.mihe.app.launcher.ui.base.BaseActivity
import com.hello.sandbox.common.Au

class SplashAct: BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Au.postDelayed(this, {
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }, 2000L)
    }
}