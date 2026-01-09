package com.hello.mihe.app.launcher.ui.base

import android.animation.ValueAnimator
import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.SystemBarStyle
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.launcher3.R
import com.hello.mihe.app.launcher.ui.base.owner.FragmentOwner

open class BaseActivity : AppCompatActivity(), FragmentOwner {

    protected var progressDialog: Dialog? = null

    fun <T : ViewModel> createViewModel(clazz: Class<T>, block: (() -> T)? = null): T {
        return ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return if (block == null) {
                    modelClass.newInstance() as T
                } else {
                    block() as T
                }
            }
        })[clazz]
    }

    fun overrideBackPress(
        enable: Boolean = true, onBackPressed: OnBackPressedCallback.() -> Unit = {}
    ) {
        // 屏蔽返回键
        onBackPressedDispatcher.addCallback(this, enable, onBackPressed)
    }


    override fun hostActivity() = this

    override fun showLoading() {
        progress("")?.show()
    }

    override fun showLoading(describe: String) {
        progress(describe)?.show()
    }

    override fun hideLoading() {
        progressDismiss()
    }

    open fun getPageName(): String? = this.javaClass.simpleName
    open fun progress(id: Int, delay: Boolean): Dialog? {
        return progress(getString(id), delay)
    }

    open fun progress(text: String?): Dialog? {
        return progress(text, false)
    }

    open fun progress(text: String?, delay: Boolean): Dialog? {
        return progress(text, delay, true)
    }

    open fun progress(describe: String?, delay: Boolean, animateDim: Boolean): Dialog? {
        return if (progressDialog == null && !isFinishing) {
            val p: Dialog = progress(this, describe)
            p.show()
            progressDialog = p
            if (delay) {
                val animator = ValueAnimator.ofFloat(0f, 1f)
                val lp = p.window!!.attributes
                lp.alpha = 0f
                val finalDim = lp.dimAmount
                if (animateDim) {
                    lp.dimAmount = 0f
                }
                p.window!!.attributes = lp
                animator.addUpdateListener { animation: ValueAnimator ->
                    if (progressDialog === p) {
                        val lp1 = p.window!!.attributes
                        lp1.alpha = animation.animatedFraction
                        if (animateDim) {
                            lp1.dimAmount = finalDim
                        }
                        p.window!!.attributes = lp
                    }
                }
                animator.startDelay = 400
                animator.duration = 150
                animator.interpolator = DecelerateInterpolator(1.5f)
                animator.start()
            }
            p
        } else {
            progressDialog
        }
    }

    open fun progress(act: Activity?, describe: String?): Dialog {
        val view: View
        if (TextUtils.isEmpty(describe)) {
            view = LayoutInflater.from(act).inflate(com.hello.sandbox.common.R.layout.progress_only_dialog, null)
        } else {
            view = LayoutInflater.from(act).inflate(com.hello.sandbox.common.R.layout.progress_dialog, null)
            val textDescribe = view.findViewById<TextView>(com.hello.sandbox.common.R.id.message)
            if (textDescribe != null) {
                if (TextUtils.isEmpty(describe)) {
                    textDescribe.visibility = View.GONE
                } else {
                    textDescribe.visibility = View.VISIBLE
                    textDescribe.text = describe
                }
            }
        }

        val p =
            AlertDialog.Builder(act!!, R.style.progress_dialog)
                .setView(view)
                .setCancelable(false)
                .create()
        p.setCanceledOnTouchOutside(false)
        return p
    }

    open fun progressDismiss() {
        if (progressDialog != null) {
            try {
                progressDialog!!.dismiss()
            } catch (ignore: Exception) {
            }
            progressDialog = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { true },
            SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT)
        )
    }

    private fun imm(): InputMethodManager {
        return getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    }

    fun hideInput() {
        hideInput(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
    }

    fun isShowInput() =
        imm().isActive


    private fun hideInput(flag: Int) {
        try {
            window.setSoftInputMode(flag)
            if (window.currentFocus != null) {
                imm().hideSoftInputFromWindow(window.currentFocus!!.windowToken, 0)
            } else {
                imm().hideSoftInputFromWindow(window.decorView.windowToken, 0)
            }
        } catch (e: Exception) {
        }
    }
}
