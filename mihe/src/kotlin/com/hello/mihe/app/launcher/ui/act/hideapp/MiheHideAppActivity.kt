package com.hello.mihe.app.launcher.ui.act.hideapp

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import app.lawnchair.util.isDefaultLauncher
import com.android.launcher3.R
import com.android.launcher3.databinding.MiheActivityHideAppsBinding
import com.android.launcher3.databinding.MiheItemSearchPackageBinding
import com.android.launcher3.databinding.MiheItemSuggestPackageBinding
import com.hello.mihe.app.launcher.autotracker.SensorsAnalyticsSdkHelper
import com.hello.mihe.app.launcher.autotracker.TrackUtil
import com.hello.mihe.app.launcher.event.HideAppEvent
import com.hello.mihe.app.launcher.singleClickListener
import com.hello.mihe.app.launcher.ui.act.feedback.FeedBackScorePopup
import com.hello.mihe.app.launcher.ui.act.home.SwitchLauncherDialogActivity
import com.hello.mihe.app.launcher.ui.base.BaseActivity
import com.hello.mihe.app.launcher.view.BasePopup
import com.hello.mihe.app.launcher.view.adapter.BaseAdapter
import com.hello.mihe.app.launcher.view.adapter.BaseHolder
import com.hello.mihe.app.launcher.view.adapter.BaseItemClickListener
import com.hello.mihe.app.launcher.view.showPopup
import com.hello.sandbox.common.util.MetricsUtil
import com.hello.sandbox.common.util.ToastUtil
import com.hello.sandbox.common.util.Vu
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import org.greenrobot.eventbus.EventBus


class MiheHideAppActivity : BaseActivity() {
    private val binding by lazy {
        MiheActivityHideAppsBinding.inflate(layoutInflater)
    }
    private val viewModel by lazy { createViewModel(MiheHideAppActivityViewModel::class.java) }
    private val allSystemInstallAppInfos = mutableListOf<AppExt>()
    private val adapter = SystemInstallAppAdapter(object : BaseItemClickListener<AppExt> {
        override fun onClick(binding: BaseHolder<AppExt, ViewBinding, BaseAdapter<AppExt>>, positionData: AppExt, position: Int) {
            val viewBinding = binding.binding as MiheItemSearchPackageBinding
            positionData.isSelected = !positionData.isSelected
            viewBinding.itemZfileListFileCheckBox.isSelected = positionData.isSelected
            checkSuggestAdapter()
            if (positionData.isSelected) {
                TrackUtil.track {
                    SensorsAnalyticsSdkHelper.getInstance().trackMC(
                        "l_e_select_app_click",
                        TrackUtil.getTrackAppInfo(positionData.app.key.componentName.packageName),
                    )
                }
            }
        }
    })
    private val suggestAdapter = SuggestAdapter(object : BaseItemClickListener<AppExt> {
        override fun onClick(binding: BaseHolder<AppExt, ViewBinding, BaseAdapter<AppExt>>, positionData: AppExt, position: Int) {
            val viewBinding = binding.binding as MiheItemSuggestPackageBinding
            positionData.isSelected = !positionData.isSelected
            viewBinding.itemZfileListFileCheckBox.isSelected = positionData.isSelected
            checkAdapter()
        }
    })
    private var startPermissionActivity: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        SensorsAnalyticsSdkHelper.getInstance().trackPV("l_p_app_list")
        checkPermission()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
        binding.header.headerRecyclerview.addItemDecoration(object : RecyclerView.ItemDecoration() {

            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                outRect.left = MetricsUtil.DP_28;
            }
        })
        binding.header.headerRecyclerview.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        binding.header.headerRecyclerview.adapter = suggestAdapter

        viewModel.systemApps.observe(this) {
            adapter.clear()
            adapter.addAll(it)
            adapter.notifyDataSetChanged()
            allSystemInstallAppInfos.clear()
            allSystemInstallAppInfos.addAll(it)
            binding.rlList.visibility = View.VISIBLE
            hideLoading()
            binding.searchHeader.root.visibility = View.VISIBLE
        }
        viewModel.suggestApps.observe(this) {
            suggestAdapter.clear()
            suggestAdapter.addAll(it)
            suggestAdapter.notifyDataSetChanged()
            hideLoading()
            if (suggestAdapter.getData().isEmpty()) {
                binding.header.root.visibility = View.GONE
            } else {
                binding.header.root.visibility = View.VISIBLE
            }
        }
        viewModel.filterApp.observe(this) {
            adapter.clear()
            adapter.addAll(it)
            adapter.notifyDataSetChanged()
        }
        viewModel.hideApp.observe(this) {

            hideLoading()
            EventBus.getDefault().post(HideAppEvent())
            finish()
        }
        binding.sidebar.setSideBarLayoutListener {
            val mAppList = adapter.getData()
            for (i in mAppList.indices) {
                if (mAppList[i].firstLetter == it) {
                    (binding.recyclerView.layoutManager!! as LinearLayoutManager).scrollToPositionWithOffset(i, 0)
                    break
                }
            }
        }
        binding.searchHeader.editSearch.addTextChangedListener { text ->
            viewModel.filterApp(text, allSystemInstallAppInfos)
        }
        binding.searchHeader.editSearch.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                binding.searchHeader.cancel.visibility = View.VISIBLE
                binding.header.root.visibility = View.GONE
            }else{
                binding.searchHeader.cancel.visibility = View.GONE
                binding.header.root.visibility = View.VISIBLE
            }
        }
        binding.searchHeader.editSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId === EditorInfo.IME_ACTION_SEARCH) {
                hideInput()
            }
            binding.searchHeader.editSearch.post {
                binding.searchHeader.editSearch.requestFocus()
            }
            false
        }
        binding.searchHeader.cancel.singleClickListener {
            binding.searchHeader.editSearch.text.clear()
            binding.searchHeader.editSearch.clearFocus()
            hideInput()
        }
        binding.vnNavigationbar.setLeftIconOnClick {
            finish()
        }
        binding.btnHideApp.singleClickListener {
            val data = adapter.getData()
            val selectApp = ArrayList<String>()
            for (appInfo in data) {
                if (appInfo.isSelected) {
                    selectApp.add(appInfo.app.key.componentName.packageName)
                }
            }
            TrackUtil.track {
                val trackAppInfo = TrackUtil.getTrackAppInfo(selectApp)
                SensorsAnalyticsSdkHelper.getInstance().trackMC("l_e_hide_app_confirm_click", trackAppInfo)
            }
            if (selectApp.isEmpty()) {
                ToastUtil.message(getString(R.string.mihe_unselect_app))
                return@singleClickListener
            }
            if (isDefaultLauncher()) {
                showLoading()
                viewModel.hideApp(this, adapter)
                TrackUtil.track {
                    val trackAppInfo = TrackUtil.getTrackAppInfo(selectApp)
                    SensorsAnalyticsSdkHelper.getInstance().trackMC("l_e_hide_app_success", trackAppInfo)
                }
            } else {
                BasePopup(
                    this,
                    getString(R.string.mihe_set_default_launcher_tile),
                    getString(R.string.mihe_set_default_launcher_desc_1),
                    {
                        viewModel.perHideApp(this, adapter)
                        SensorsAnalyticsSdkHelper.getInstance().trackMC("l_e_guide_set_default_desktop_confirm")
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
                    },
                    { },
                    getString(R.string.mihe_set_default_launcher_comfirm),
                    getString(R.string.mihe_set_default_launcher_cancel),
                    false,
                ).showPopup(this)
            }
        }
    }


    private fun checkPermission() {
        if (!XXPermissions.isGranted(this, Permission.GET_INSTALLED_APPS)) {
            showErrorView()
        } else {
            startPermissionActivity = false
            showLoading()
            viewModel.loadApps(this@MiheHideAppActivity)
            binding.include.root.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        if (
            startPermissionActivity &&
            binding.include.root.visibility == View.VISIBLE
        ) {
            checkPermission()
        }
    }

    private fun showErrorView() {
        binding.include.imgTip.setImageResource(R.drawable.icon_mihe_main_no_data)
        binding.include.tvTitleTip.setText(R.string.prompt_popup_title)
        binding.include.tvTitleDesc.setText(R.string.mihe_show_get_installed_app_tip)
        binding.include.btnAction.setText(R.string.mihe_incompatibility_open_now)
        binding.include.btnAction.singleClickListener {
            requestGetInstalledAppsPermissions()
        }
    }

    private fun requestGetInstalledAppsPermissions() {
        XXPermissions.with(this)
            .permission(Permission.GET_INSTALLED_APPS)
            .request(
                object : OnPermissionCallback {
                    override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
                        showLoading()
                        viewModel.loadApps(this@MiheHideAppActivity)
                        binding.include.root.visibility = View.GONE
                    }

                    override fun onDenied(permissions: MutableList<String>, doNotAskAgain: Boolean) {
                        if (doNotAskAgain) {
                            XXPermissions.startPermissionActivity(this@MiheHideAppActivity, permissions)
                            startPermissionActivity = true
                        }
                    }
                }
            )
    }


    private fun checkAdapter() {
        adapter.notifyDataSetChanged()
    }

    private fun checkSuggestAdapter() {
        suggestAdapter.notifyDataSetChanged()
    }
}
