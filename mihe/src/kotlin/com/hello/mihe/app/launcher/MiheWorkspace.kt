package com.hello.mihe.app.launcher

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.view.doOnLayout
import app.lawnchair.preferences2.PreferenceManager2
import app.lawnchair.util.firstBlocking
import com.android.launcher3.BubbleTextView
import com.android.launcher3.Launcher
import com.android.launcher3.R
import com.android.launcher3.databinding.HideAppSuccessTipPopupBinding
import com.android.launcher3.model.data.ItemInfo
import com.android.launcher3.popup.PopupContainerWithArrow
import com.hello.mihe.app.launcher.config.needShowHideAppSuccessPopup
import com.hello.mihe.app.launcher.config.needShowHideAppSuccessTip
import com.hello.mihe.app.launcher.config.saveNeedShowHideAppSuccessPopup
import com.hello.mihe.app.launcher.config.saveNeedShowHideAppSuccessTip
import com.hello.mihe.app.launcher.utils.Vlog
import com.hello.sandbox.common.util.MetricsUtil

object MiheWorkspace {
    private var needShowHideAppSuccessTip = needShowHideAppSuccessTip()
    private var needShowHideAppSuccessPopup = needShowHideAppSuccessPopup()
    private const val TAG = "MiheWorkspace"

    @JvmStatic
    fun checkShowHideAppSuccess(child: View) {
        if (!needShowHideAppSuccessTip && !needShowHideAppSuccessPopup) return
        if (child !is BubbleTextView) return
        val tag = child.tag
        if (tag is ItemInfo) {
            val componentKey = tag.componentKey ?: return
            if ("com.hello.mihe.app.launcher" == componentKey.componentName.packageName &&
                "com.hello.mihe.app.launcher.ui.act.login.SplashAct" == componentKey.componentName.className && componentKey.user.hashCode() == 0
            ) {
                Vlog.d(TAG, componentKey.toString())
                child.doOnLayout {
                    showHideAppSuccess(child)
                }
            }
        }
    }

    private fun showHideAppSuccessForIcon(child: BubbleTextView) {
        if (!needShowHideAppSuccessTip) {
            return
        }
        val launcher = Launcher.getLauncher(child.context)
        val container = launcher.layoutInflater
            .inflate(
                R.layout.hide_app_success_tip_popup_container, launcher.dragLayer, false
            ) as PopupContainerWithArrow<*>
        container.populateAndShow(child)
        container.show()
        saveNeedShowHideAppSuccessTip()
        needShowHideAppSuccessTip = false
    }

    private fun showHideAppSuccess(child: BubbleTextView) {
        if (needShowHideAppSuccessPopup) {
            val preHiddenApps = PreferenceManager2.getInstance(MiheApp.app).preHiddenApps.get().firstBlocking()
            if (preHiddenApps.isEmpty()) {
                showHideAppSuccessForIcon(child)
                return
            }
            val launcher = Launcher.getLauncher(child.context)
            val popupBinding = HideAppSuccessTipPopupBinding.inflate(launcher.layoutInflater, launcher.dragLayer, false)
            popupBinding.root.measure(
                View.MeasureSpec.makeMeasureSpec(MetricsUtil.dp(205f), View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(MetricsUtil.dp(73f), View.MeasureSpec.UNSPECIFIED)
            )
            val popup = PopupWindow(
                popupBinding.root, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            popup.isOutsideTouchable = false
            popup.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
            val location = IntArray(2)
            launcher.dragLayer.getLocationOnScreen(location)
            popup.showAtLocation(
                launcher.dragLayer, Gravity.NO_GRAVITY,
                launcher.dragLayer.width / 2 - popupBinding.root.measuredWidth / 2,
                launcher.dragLayer.height / 2 - popupBinding.root.measuredHeight / 2
            )
            launcher.dragLayer.postDelayed({
                showHideAppSuccessForIcon(child)
            }, 1000)
            launcher.dragLayer.postDelayed({
                popup.dismiss()
            }, 3000)
            needShowHideAppSuccessPopup = false
            saveNeedShowHideAppSuccessPopup()
            return
        }
        showHideAppSuccessForIcon(child)
    }
}