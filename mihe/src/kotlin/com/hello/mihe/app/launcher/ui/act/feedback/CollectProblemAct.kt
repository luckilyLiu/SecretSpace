package com.hello.mihe.app.launcher.ui.act.feedback

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import androidx.compose.ui.util.fastJoinToString
import com.android.launcher3.R
import com.android.launcher3.databinding.MiheActivityFeedbackCollectProblemBinding
import com.hello.mihe.app.launcher.api.models.BaseResponse
import com.hello.mihe.app.launcher.api.models.FeedbackDefaultReasonResponse
import com.hello.mihe.app.launcher.autotracker.SensorsAnalyticsSdkHelper
import com.hello.mihe.app.launcher.config.saveNeedShowFeedBackPopupState
import com.hello.mihe.app.launcher.singleClickListener
import com.hello.mihe.app.launcher.ui.base.BaseActivity
import com.hello.mihe.app.launcher.utils.LanguageUtil
import com.hello.mihe.app.launcher.utils.SoftHideKeyBoardUtil
import com.hello.sandbox.common.util.ToastUtil
import org.json.JSONObject

private const val TAG = "CollectProblemAct"
private const val START_COLLECT_PROBLEM_ACT_PARMA_SCORE = "start_collect_problem_act_parma_score"

fun startCollectProblemAct(context: Context, score: Int) {
    val intent = Intent(context, CollectProblemAct::class.java)
    intent.putExtra(START_COLLECT_PROBLEM_ACT_PARMA_SCORE, score.toString())
    context.startActivity(intent)
}

class CollectProblemAct : BaseActivity() {
    private val viewModel by lazy { createViewModel(CollectProblemViewModel::class.java) }
    private val binding by lazy { MiheActivityFeedbackCollectProblemBinding.inflate(layoutInflater) }
    private var score = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        kotlin.runCatching {
            val resources: Resources = Resources.getSystem()
            val resourceId: Int = resources.getIdentifier("status_bar_height", "dimen", "android")
            binding.root.setPadding(0, resources.getDimensionPixelSize(resourceId), 0, 0)
        }
        setContentView(binding.root)
        showLoading()
        viewModel.getFeedbackDefaultReasonConfig()
        viewModel.feedbackDefaultReasonConfigObserver.observe(this) {
            getFeedbackReason(it).forEach {
                val textView =
                    layoutInflater.inflate(R.layout.popup_feedback_collect_problem_tag_item, null) as TextView
                textView.text = it
                binding.cgProblem.addView(textView)
                textView.singleClickListener { textView.isSelected = !textView.isSelected }
            }
            hideLoading()
        }

        SoftHideKeyBoardUtil.assistActivity(
            this,
            object : SoftHideKeyBoardUtil.OnSoftInputChangedListener {
                override fun onSoftInputOpen() {
                    binding.btnNext.visibility = View.GONE
                }

                override fun onSoftInputClose() {
                    binding.btnNext.visibility = View.VISIBLE
                }
            }
        )
        binding.etInput.addTextChangedListener(
            object : TextWatcher {
                private val maxLength = 800
                private var destCount: Int = 0
                private var dStart: Int = 0
                private var dEnd: Int = 0
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    destCount = LanguageUtil.calcTextLength(s)
                    // 获取输入字符的起始位置
                    dStart = start
                    // 获取输入字符的个数
                    dEnd = start + after
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    // count是输入后的字符长度
                    val count = LanguageUtil.calcTextLength(s)
                    if (count > maxLength) {
                        // 超过了sum个字符，需要截取
                        val sum = count - maxLength
                        val delete = LanguageUtil.getDeleteIndex(s!!, dStart, dEnd, sum)
                        if (delete < dEnd) {
                            // 输入字符超过了限制，截取
                            s.delete(delete, dEnd)
                        }
                    }
                    s?.let { binding.tvTextCount.text = "${maxLength - LanguageUtil.calcTextLength(s)}" }
                }
            }
        )
        binding.vnNavigationbar.setLeftIconOnClick { finish() }
        score = intent.getStringExtra(START_COLLECT_PROBLEM_ACT_PARMA_SCORE) ?: ""
        viewModel.pushFeedBackProblemObserver.observe(this) {
            hideLoading()
            if (it.isSuccess()) {
                ToastUtil.alert(R.string.mihe_collect_problem_thank_you_for_your_feedback)
                saveNeedShowFeedBackPopupState(3)
                finish()
            } else {
                ToastUtil.alert(R.string.mihe_network_error)
            }
        }

        binding.btnNext.singleClickListener {
            val problems = getProblems()
            val discuss = binding.etInput.text.toString()
            if (problems.isEmpty() && discuss.isNullOrEmpty()) {
                ToastUtil.alert(R.string.mihe_collect_problem_please_select_reason)
                return@singleClickListener
            }
            showLoading()
            viewModel.pushFeedBackProblem(score, problems, discuss)
            SensorsAnalyticsSdkHelper.getInstance().trackMC("e_submit_star_rating_continue", JSONObject().apply {
                put("star_rating", score)
                put("reson_of_dissatisfaction", problems.fastJoinToString(","))
                put("user_suggestion", discuss)
            })
        }
        binding.nsContent.singleClickListener {
            if (binding.btnNext.visibility == View.GONE) {
                hideInput()
            }
        }
    }

    private fun getProblems(): List<String> {
        val result = mutableListOf<String>()
        for (index in 0 until binding.cgProblem.childCount) {
            val textView = binding.cgProblem.getChildAt(index) as TextView
            if (textView.isSelected) {
                result.add(textView.text.toString())
            }
        }
        return result
    }

    private fun getFeedbackReason(response: BaseResponse<FeedbackDefaultReasonResponse>?): Array<String> {
        if (response == null || !response.isSuccess() || response.data == null) return resources.getStringArray(R.array.mihe_feedback_default_reason)
        return try {
            if (LanguageUtil.languageIsEn()) {
                response.data!!.en.toTypedArray()
            } else {
                response.data!!.cn.toTypedArray()
            }
        } catch (th: Throwable) {
            resources.getStringArray(R.array.mihe_feedback_default_reason)
        }
    }
}



