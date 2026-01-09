package com.hello.mihe.app.launcher.ui.act.home

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.TextView
import com.android.launcher3.R
import com.hello.mihe.app.launcher.ui.base.BaseActivity
import com.hello.mihe.app.launcher.utils.LanguageUtil
import com.opensource.svgaplayer.SVGADrawable
import com.opensource.svgaplayer.SVGAImageView
import com.opensource.svgaplayer.SVGAParser
import com.opensource.svgaplayer.SVGAVideoEntity


class SwitchLauncherDialogActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mihe_switch_launcher_dialog)
        val svgaImageView = findViewById<SVGAImageView>(R.id.svga)
        findViewById<ImageView>(R.id.switch_launcher_close).setOnClickListener({ finish() })
        if (LanguageUtil.languageIsCn()) {
            parserSource("switch_launcher_button.svga", svgaImageView)
        } else {
            parserSource("switch_launcher_button_en.svga", svgaImageView)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        finish()
        return super.onTouchEvent(event)
    }

    private fun parserSource(source: String, svgaImageView: SVGAImageView) {
        val parser = SVGAParser(this)
        parser.decodeFromAssets(
            source,
            object : SVGAParser.ParseCompletion {
                override fun onComplete(videoItem: SVGAVideoEntity) {
                    val svgaDrawable = SVGADrawable(videoItem)
                    svgaImageView.setImageDrawable(svgaDrawable)
                    svgaImageView.startAnimation()
                }

                override fun onError() {
                }
            },
        )
    }

    override fun onPause() {
        super.onPause()
      kotlin.runCatching {
          finishAndRemoveTask()
      }
    }
}
