package com.hello.mihe.app.launcher.ui.act.home

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import app.lawnchair.preferences2.PreferenceManager2
import app.lawnchair.ui.preferences.PreferenceActivity
import app.lawnchair.ui.preferences.navigation.Routes
import app.lawnchair.util.App
import app.lawnchair.util.isDefaultLauncher
import com.android.launcher3.R
import com.android.launcher3.databinding.MiheMainActivityBinding
import com.android.launcher3.databinding.MiheMainTopMenuPopuBinding
import com.android.launcher3.databinding.MiheUnhideTipPopupBinding
import com.hello.mihe.app.launcher.autotracker.SensorsAnalyticsSdkHelper
import com.hello.mihe.app.launcher.autotracker.TrackUtil
import com.hello.mihe.app.launcher.config.UserManager
import com.hello.mihe.app.launcher.config.needShowFeedBackPopup
import com.hello.mihe.app.launcher.config.needShowUnHideTip
import com.hello.mihe.app.launcher.config.saveNeedShowFeedBackPopupState
import com.hello.mihe.app.launcher.config.saveShowUnHideTipState
import com.hello.mihe.app.launcher.event.BaseEvent
import com.hello.mihe.app.launcher.event.HideAppEvent
import com.hello.mihe.app.launcher.isTouchOn
import com.hello.mihe.app.launcher.singleClickListener
import com.hello.mihe.app.launcher.ui.act.feedback.FeedBackScorePopup
import com.hello.mihe.app.launcher.ui.act.hideapp.MiheHideAppActivity
import com.hello.mihe.app.launcher.ui.base.BaseActivity
import com.hello.mihe.app.launcher.utils.Vlog
import com.hello.mihe.app.launcher.view.BasePopup
import com.hello.mihe.app.launcher.view.adapter.BaseAdapter
import com.hello.mihe.app.launcher.view.adapter.BaseHolder
import com.hello.mihe.app.launcher.view.adapter.BaseItemClickListener
import com.hello.mihe.app.launcher.view.adapter.BaseItemLongClickListener
import com.hello.mihe.app.launcher.view.showPopup
import com.hello.sandbox.common.util.MetricsUtil
import com.hello.sandbox.common.util.ToastUtil
import com.lxj.xpopup.util.XPopupUtils
import com.patrykmichalik.opto.core.setBlocking
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject

class MiheMainActivity : BaseActivity() {
    private val binding by lazy {
        MiheMainActivityBinding.inflate(layoutInflater)
    }
    private val viewModel by lazy {
        createViewModel(MiheMainActivityViewModel::class.java)
    }
    private val adapter = object : MiheMainAdapter() {
        override fun onViewAttachedToWindow(holder: BaseHolder<App, ViewBinding, BaseAdapter<App>>) {
            super.onViewAttachedToWindow(holder)
            if (holder.absoluteAdapterPosition == 0 && needShowUnHideTip()) {
                holder.itemView.doOnLayout {
                    val popupBinding = MiheUnhideTipPopupBinding.inflate(layoutInflater)
                    popupBinding.root.measure(
                        View.MeasureSpec.makeMeasureSpec(MetricsUtil.dp(205f), View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(MetricsUtil.dp(73f), View.MeasureSpec.UNSPECIFIED)
                    )
                    val popup = PopupWindow(
                        popupBinding.root, ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    popup.isOutsideTouchable = true
                    popup.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
                    val location = IntArray(2)
                    holder.itemView.getLocationOnScreen(location)
                    popup.showAtLocation(
                        holder.itemView, Gravity.NO_GRAVITY, location[0] + XPopupUtils.dp2px(this@MiheMainActivity, 16f),
                        location[1] + holder.itemView.height + XPopupUtils.dp2px(this@MiheMainActivity, 6f)
                    )
                    saveShowUnHideTipState()
                }
            }
        }
    }

    private var hideTipDialog: BasePopup? = null
    private var localNeedShowFeedBackPopup = false
    private var menuPopup: PopupWindow? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initRecyclerView()
        showLoading()
        viewModel.loadHideApps(this)
        viewModel.apps.observe(this) {
            hideLoading()
            adapter.clear()
            adapter.addAll(it)
            adapter.notifyDataSetChanged()
            checkAdapter()
        }
        binding.imgAddApp1.singleClickListener {
            SensorsAnalyticsSdkHelper.getInstance().trackMC("l_e_hide_app_click")
            startActivity(Intent(this, MiheHideAppActivity::class.java))
        }
        binding.imgAddApp2.singleClickListener {
            SensorsAnalyticsSdkHelper.getInstance().trackMC("l_e_hide_app_click")
            startActivity(Intent(this, MiheHideAppActivity::class.java))
        }

        binding.imgMenu.singleClickListener {
            if (menuPopup?.isShowing == true) {
                menuPopup!!.dismiss()
                return@singleClickListener
            }
            SensorsAnalyticsSdkHelper.getInstance().trackMC("l_e_secondary_function_entrance_click")
            val popupBinding: MiheMainTopMenuPopuBinding
            if (menuPopup == null) {
                popupBinding = MiheMainTopMenuPopuBinding.inflate(layoutInflater)
                popupBinding.root.measure(
                    View.MeasureSpec.makeMeasureSpec(MetricsUtil.dp(205f), View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(MetricsUtil.dp(73f), View.MeasureSpec.UNSPECIFIED)
                )
                menuPopup = createMenuPopup(popupBinding)
            } else {
                popupBinding = MiheMainTopMenuPopuBinding.bind(menuPopup!!.contentView)
            }
            if (isDefaultLauncher()) {
                popupBinding.tvBackToOld.visibility = View.VISIBLE
            } else {
                popupBinding.tvBackToOld.visibility = View.GONE
            }
            val location = IntArray(2)
            binding.imgMenu.getLocationOnScreen(location)
            menuPopup!!.showAtLocation(
                binding.imgMenu, Gravity.NO_GRAVITY, location[0] - popupBinding.root.measuredWidth +
                        binding.imgMenu.width, location[1] + binding.imgMenu.height
            )
        }
        EventBus.getDefault().register(this)
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                Vlog.d("MiheMainActivity", "adapter onChanged")
                if (!isDefaultLauncher() && adapter.getData().isNotEmpty()) {
                    if (hideTipDialog == null)
                        hideTipDialog = BasePopup(
                            this@MiheMainActivity,
                            getString(R.string.mihe_back_to_old_launcher_title),
                            getString(R.string.mihe_hide_state_disable, getString(R.string.derived_app_name)),
                            {
                                setDefaultLauncher()
                            },
                            {
                                SensorsAnalyticsSdkHelper.getInstance().trackMC("l_e_guide_set_default_desktop_cancel")
                            },
                            getString(R.string.mihe_hide_state_comfirm),
                            getString(R.string.mihe_hide_state_cancel),
                            false,
                        )
                    if (!hideTipDialog!!.isShow) {
                        hideTipDialog!!.showPopup(this@MiheMainActivity)
                        SensorsAnalyticsSdkHelper.getInstance().trackMV("l_e_guide_set_default_desktop_popup")
                    }
                }
            }
        })
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun createMenuPopup(popupBinding: MiheMainTopMenuPopuBinding): PopupWindow {

        val popup = PopupWindow(
            popupBinding.root, ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        popup.isOutsideTouchable = true
        popup.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popup.setTouchInterceptor { v, event ->
            if (event.action == MotionEvent.ACTION_OUTSIDE) {
                if (binding.imgMenu.isTouchOn(event) && popup.isShowing)
                    return@setTouchInterceptor true
            }
            return@setTouchInterceptor false
        }
        popupBinding.tvBackToOld.singleClickListener {
            SensorsAnalyticsSdkHelper.getInstance().trackMC("l_e_switch_original_desktop_click")
            SensorsAnalyticsSdkHelper.getInstance().trackMV("l_e_switch_original_desktop_second_comfirm_popup")
            popup.dismiss()
            BasePopup(
                this,
                getString(R.string.mihe_back_to_old_launcher_title),
                getString(R.string.mihe_back_to_old_launcher_desc),
                {
                    SensorsAnalyticsSdkHelper.getInstance()
                        .trackMC("l_e_switch_original_desktop_second_comfirm_click")
                    val intent = Intent(Settings.ACTION_HOME_SETTINGS)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                },
                {
                    SensorsAnalyticsSdkHelper.getInstance()
                        .trackMC("l_e_switch_original_desktop_second_cancel_click")
                },
                getString(R.string.mihe_back_to_old_launcher_comfirm),
                getString(R.string.mihe_back_to_old_launcher_cancel),
                false,
            )
                .showPopup(this)
        }
        popupBinding.tvLauncherSetting.singleClickListener {
            SensorsAnalyticsSdkHelper.getInstance()
                .trackMC("l_e_secondary_function_desktop_set_click")
            settingClick()
            popup.dismiss()
        }
        return popup
    }

    private fun initRecyclerView() {
        binding.recyclerView.layoutManager = GridLayoutManager(this, 4)
        binding.recyclerView.adapter = adapter
        adapter.itemClickListener = object : BaseItemClickListener<App> {
            override fun onClick(binding: BaseHolder<App, ViewBinding, BaseAdapter<App>>, positionData: App, position: Int) {
                TrackUtil.track {
                    SensorsAnalyticsSdkHelper.getInstance().trackMC("l_e_open_app_click", TrackUtil.getTrackAppInfo(positionData.key.componentName.packageName))
                }
                kotlin.runCatching {
                    startActivity(
                        Intent().setComponent(positionData.key.componentName)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                }
                if (needShowFeedBackPopup()) {
                    localNeedShowFeedBackPopup = true
                }
            }

        }
        adapter.itemLongClickListener = object : BaseItemLongClickListener<App> {
            override fun onLongClick(binding: ViewBinding, positionData: App, position: Int): Boolean {
                PopupMenu(this@MiheMainActivity, binding.root).also {
                    it.inflate(R.menu.mihe_main_app_menu)
                    it.show()
                    it.setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.app_unhide -> {
                                viewModel.unHideAppByKey(this@MiheMainActivity, positionData.key.toString())
                                adapter.getData().remove(positionData)
                                adapter.notifyDataSetChanged()
                                checkAdapter()
                                ToastUtil.message(getString(R.string.mihe_unhide_app_successful))
                            }
                        }
                        return@setOnMenuItemClickListener true
                    }
                    it.show()
                }
                return true
            }
        }
    }

    private fun settingClick() {
        if (isDefaultLauncher()) {
            startActivity(PreferenceActivity.createIntent(this, Routes.SMARTSPACE))
        } else {
            SensorsAnalyticsSdkHelper.getInstance().trackMV("l_e_guide_set_default_desktop_popup")
            BasePopup(
                this,
                getString(R.string.mihe_set_default_launcher_tile),
                getString(R.string.mihe_set_default_launcher_desc),
                {
                    setDefaultLauncher()
                },
                {
                    SensorsAnalyticsSdkHelper.getInstance().trackMC("l_e_guide_set_default_desktop_cancel")
                },
                getString(R.string.mihe_set_default_launcher_comfirm),
                getString(R.string.mihe_set_default_launcher_cancel),
                false,
            )
                .showPopup(this)
        }
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: BaseEvent) {
        if (event is HideAppEvent) {
            showLoading()
            viewModel.loadHideApps(this)
        }
    }

    override fun onResume() {
        super.onResume()
        val jsonObject = JSONObject()
        jsonObject.put("country_zh", UserManager.countryZh)
        SensorsAnalyticsSdkHelper.getInstance().trackPV("l_p_internal_homepage", jsonObject)
        adapter.showHideIcon = isDefaultLauncher()
        adapter.notifyDataSetChanged()
        if (needShowFeedBackPopup() && localNeedShowFeedBackPopup) {
            localNeedShowFeedBackPopup = false
            FeedBackScorePopup(this).showPopup(this)
            saveNeedShowFeedBackPopupState()
        }
    }

    private fun checkAdapter() {
        if (adapter.getData().isEmpty()) {
            binding.imgAddApp1.visibility = View.VISIBLE
            binding.tvNoHideApp.visibility = View.VISIBLE
            binding.tvClickToHideApp.visibility = View.VISIBLE
            binding.imgAddApp2.visibility = View.GONE
        } else {
            TrackUtil.track {
                SensorsAnalyticsSdkHelper.getInstance()
                    .trackMC("l_e_hide_app_list", TrackUtil.getTrackAppInfo(adapter.getData().map { it.key.componentName.packageName }))
            }
            binding.imgAddApp1.visibility = View.GONE
            binding.tvNoHideApp.visibility = View.GONE
            binding.tvClickToHideApp.visibility = View.GONE
            binding.imgAddApp2.visibility = View.VISIBLE
        }
    }

    private fun setDefaultLauncher() {
        SensorsAnalyticsSdkHelper.getInstance().trackMC("l_e_guide_set_default_desktop_confirm")
        PreferenceManager2.getInstance(this).preHiddenApps.setBlocking(emptySet())
        val intent = Intent(Settings.ACTION_HOME_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(
            intent,
            ActivityOptions.makeCustomAnimation(this, R.anim.mihe_no_anim, R.anim.mihe_no_anim)
                .toBundle()
        )
        binding.recyclerView.postDelayed(
            {
                startActivity(
                    Intent(this, SwitchLauncherDialogActivity::class.java).apply {
                        this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    },
                    ActivityOptions.makeCustomAnimation(
                        this,
                        R.anim.mihe_enter_anim,
                        R.anim.mihe_enter_anim
                    )
                        .toBundle()
                )
            },
            500
        )
    }
}
