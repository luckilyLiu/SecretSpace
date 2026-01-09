package com.hello.mihe.app.launcher.view

import android.content.Context
import android.os.CountDownTimer
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.android.launcher3.R
import com.hello.sandbox.common.util.ViewUtil
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.core.CenterPopupView
import com.lxj.xpopup.util.XPopupUtils

open class BasePopup(
  context: Context,
  var name: String,
  var description: String?,
  var confirmRunnable: Runnable?,
  var cancelRunnable: Runnable?,
  private var confirm: String,
  private var cancel: String,
  private var needShowClose: Boolean = true,
  private val closeRunnable: Runnable? = null,
  private val needShowCancelButton: Boolean = true,
  private val needTimeCount: Boolean = false,
  private val showTitle: Boolean = true
) : CenterPopupView(context) {

  override fun getImplLayoutId(): Int {
    return R.layout.mihe_popup_base_layout
  }

  private lateinit var textTitle: TextView
  private lateinit var textDescription: TextView
  private lateinit var confirmButton: Button
  private lateinit var cancelButton: TextView
  private lateinit var closeImg: ImageView
  private var countDownTimer: CountDownTimer? = null
  override fun onCreate() {
    super.onCreate()
    textTitle = findViewById(R.id.title)
    textDescription = findViewById(R.id.description)
    confirmButton = findViewById(R.id.btn_confirm)
    cancelButton = findViewById(R.id.btn_cancel)
    closeImg = findViewById(R.id.img_close)
    if (!needShowClose) {
      closeImg.visibility = INVISIBLE
    }
    if (!showTitle) {
      textTitle.visibility = GONE
    }
    textTitle.text = name
    textDescription.text = description
    confirmButton.text = confirm
    if (!needShowCancelButton) {
      cancelButton.visibility = GONE
      findViewById<View>(R.id.view_gap).visibility = GONE
    }
    cancelButton.text = cancel
    ViewUtil.singleClickListener(confirmButton) {
      confirmRunnable?.run()
      dismiss()
    }
    ViewUtil.singleClickListener(cancelButton) {
      cancelRunnable?.run()
      dismiss()
    }
    ViewUtil.singleClickListener(closeImg) {
      dismiss()
      closeRunnable?.run()
    }
    if (needTimeCount) {
      textDescription.gravity = Gravity.LEFT
      textTitle.gravity = Gravity.LEFT
      confirmButton.isClickable = false
      confirmButton.text = "$confirm(5)"
      countDownTimer =
        object : CountDownTimer(5000, 1000) {
          override fun onTick(millisUntilFinished: Long) {
            confirmButton.text = "$confirm(${millisUntilFinished /1000+1})"
          }

          override fun onFinish() {
            confirmButton.isClickable = true
            confirmButton.text = confirm
          }
        }
      countDownTimer?.start()
    }
  }

  override fun dismiss() {
    countDownTimer?.cancel()
    super.dismiss()
  }
}

fun CenterPopupView.showPopup(context: Context) {
  XPopup.Builder(context)
    .moveUpToKeyboard(false)
    .maxWidth(XPopupUtils.getScreenWidth(context) - XPopupUtils.dp2px(context, 40f))
    .isDestroyOnDismiss(true)
    .dismissOnTouchOutside(false)
    .dismissOnBackPressed(false)
    .asCustom(this)
    .show()
}

fun BottomPopupView.showPopup(
  context: Context,
  dismissOnTouchOutside: Boolean = false,
  dismissOnBackPressed: Boolean = false
) {
  XPopup.Builder(context)
    .moveUpToKeyboard(false)
    .isDestroyOnDismiss(true)
    .dismissOnTouchOutside(dismissOnTouchOutside)
    .dismissOnBackPressed(dismissOnBackPressed)
    .asCustom(this)
    .show()
}
