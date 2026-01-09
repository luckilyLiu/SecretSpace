package com.hello.mihe.app.launcher.ui.act.feedback

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import com.android.launcher3.R
import com.hello.mihe.app.launcher.autotracker.SensorsAnalyticsSdkHelper
import com.hello.mihe.app.launcher.config.saveNeedShowFeedBackPopupState
import com.hello.mihe.app.launcher.singleClickListener
import com.hello.mihe.app.launcher.utils.MarketHelper
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.util.XPopupUtils
import org.json.JSONObject

class FeedBackScorePopup(context: Context) : BottomPopupView(context) {
    private var scoreNow = 0
    private val scoreInfos =
        mutableListOf(
            Pair(R.id.ll_score_img_1, 1),
            Pair(R.id.ll_score_img_2, 2),
            Pair(R.id.ll_score_img_3, 3),
            Pair(R.id.ll_score_img_4, 4),
            Pair(R.id.ll_score_img_5, 5)
        )

    override fun getImplLayoutId(): Int {
        return R.layout.mihe_popup_feedback_score
    }

    override fun onCreate() {
        super.onCreate()
        val width =
            (XPopupUtils.getScreenWidth(context) -
                    XPopupUtils.dp2px(context, 64f) -
                    XPopupUtils.dp2px(context, 9.5f) * 4) / 5
        scoreInfos.forEach { setScoreItem(width, it.first, it.second) }
        findViewById<View>(R.id.img_close).singleClickListener {
            dismiss()
            SensorsAnalyticsSdkHelper.getInstance().trackMC("e_star_riting_close")
        }
        SensorsAnalyticsSdkHelper.getInstance().trackMV("e_star_riting_popup")
    }

    private fun rateApp(context: Context) {
        try {
            val intent =
                MarketHelper.getGoToAppMargetIntent(context, context.packageName, false)
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context.packageName)
                )
            )
        }
    }

    private fun onScoreChange(score: Int) {
        scoreNow = score
        scoreInfos.forEach {
            val view = findViewById<View>(it.first)
            view.isSelected = score >= it.second
        }
        if (scoreNow < 1) {
            return
        }
        SensorsAnalyticsSdkHelper.getInstance().trackMC("e_submit_star_rating_click", JSONObject().apply {
            put("star_rating", scoreNow)
        })

        if (scoreNow >= 4) {
            kotlin.runCatching {
                rateApp(context)
                saveNeedShowFeedBackPopupState(3)
            }
        } else {
            startCollectProblemAct(context, scoreNow)
        }
        dismiss()
    }

    private fun setScoreItem(width: Int, id: Int, score: Int) {
        val view = findViewById<View>(id)
        val lp = view.layoutParams
        lp.width = width
        view.layoutParams = lp
        view.setOnClickListener { onScoreChange(score) }
    }
}
