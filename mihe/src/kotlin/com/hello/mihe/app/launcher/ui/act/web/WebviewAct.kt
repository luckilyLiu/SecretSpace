package com.hello.mihe.app.launcher.ui.act.web

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.android.launcher3.databinding.MiheActivityWebviewBinding
import com.hello.mihe.app.launcher.ui.base.BaseActivity

class WebviewAct : BaseActivity() {
    private val binding by lazy {
        MiheActivityWebviewBinding.inflate(layoutInflater)
    }
    private var url: String? = null

    companion object {
        const val paramUrl = "url"
        const val paramTitle = "title"
        fun start(
            context: Context,
            url: String,
            title: String?,
            startFromProfileOwner: Boolean = false
        ) {
            val intent = Intent(context, WebviewAct::class.java)
            intent.putExtra(paramUrl, url)
            intent.putExtra(paramTitle, title)
            context.packageManager.setComponentEnabledSetting(
                intent.component!!,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.vnNavigationbar.setLeftIconOnClick { finish() }
        url = intent.getStringExtra(paramUrl)
        title = intent.getStringExtra(paramTitle)
        binding.vnNavigationbar.setTitle(title)

        initWebview()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebview() {
        val webChromeClient = WebChromeClient()
        val client: WebViewClient =
            object : WebViewClient() {
                override fun onRenderProcessGone(view: WebView, detail: RenderProcessGoneDetail): Boolean {
                    return true
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    if (request?.url?.toString().equals(url)) {
                        binding.webview.visibility = View.GONE
                        binding.errorWeb.visibility = View.VISIBLE
                    }
                }

                private fun launchUrl(url: String) {
                    kotlin.runCatching {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.setData(Uri.parse(url))
                        this@WebviewAct.startActivity(intent)
                    }
                }

                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    if (
                        "https://t.me/+7g2rtnlFyYpjNmM1" == url ||
                        "https://discord.gg/mjmKdSKcsg" == url ||
                        "https://chat.whatsapp.com/JyBo8AROu22ImlXxu4R7la" == url
                    ) {
                        launchUrl(url)
                        return true
                    }
                    return super.shouldOverrideUrlLoading(view, url)
                }
            }
        binding.webview.webChromeClient = webChromeClient
        binding.webview.webViewClient = client
        val settings: WebSettings = binding.webview.settings
        settings.javaScriptEnabled = true
        settings.builtInZoomControls = true
        settings.displayZoomControls = false
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        settings.domStorageEnabled = true
        settings.loadsImagesAutomatically = true
        settings.blockNetworkImage = false
        settings.blockNetworkLoads = false
        settings.loadWithOverviewMode = false
        settings.allowFileAccess = true
        // 请勿随意改动 viewport 基础设置，原因请看 ->
        // https://confluence.p1staff.com/pages/viewpage.action?pageId=91063000
        // 请勿随意改动 viewport 基础设置，原因请看 ->
        // https://confluence.p1staff.com/pages/viewpage.action?pageId=91063000
        settings.useWideViewPort = true
        // for image loading with http
        // for image loading with http
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        binding.webview.isHorizontalScrollBarEnabled = false
        settings.userAgentString = Build.BRAND

        if (Build.VERSION.SDK_INT >= 21) {
            settings.mixedContentMode = 0
            binding.webview.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        } else if (Build.VERSION.SDK_INT >= 19) {
            binding.webview.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        } else if (Build.VERSION.SDK_INT >= 11 && Build.VERSION.SDK_INT < 19) {
            binding.webview.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        }


        url?.let { binding.webview.loadUrl(it) }
    }
}
