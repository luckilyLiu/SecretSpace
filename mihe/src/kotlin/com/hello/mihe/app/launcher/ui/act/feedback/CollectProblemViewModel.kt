package com.hello.mihe.app.launcher.ui.act.feedback

import com.hello.mihe.app.launcher.api.Api
import com.hello.mihe.app.launcher.api.models.CommitFeedBackRequest
import com.hello.mihe.app.launcher.api.models.FeedbackDefaultReasonResponse
import com.hello.mihe.app.launcher.api.models.RemoteConfigRequest
import com.hello.mihe.app.launcher.config.RemoteConfig
import com.hello.mihe.app.launcher.config.UserManager
import com.hello.mihe.app.launcher.ui.base.BaseViewModel
import com.hello.mihe.app.launcher.ui.base.StateLiveData
import com.hello.mihe.app.launcher.utils.AdUtil
import com.hello.mihe.app.launcher.utils.EncryptUtil
import java.util.Locale

class CollectProblemViewModel : BaseViewModel() {
    val pushFeedBackProblemObserver = StateLiveData<Boolean>()
    val feedbackDefaultReasonConfigObserver = StateLiveData<FeedbackDefaultReasonResponse>()
    fun pushFeedBackProblem(grade: String, problems: List<String>, discuss: String) {
        launchOnUI {
            val resul = httpCall {
                val timeStamp = System.currentTimeMillis() / 1000
                val selected = problems.joinToString(",")
                val language = Locale.getDefault().language
                val newUserRequest =
                    CommitFeedBackRequest(
                        UserManager.guestId,
                        Api.appID,
                        timeStamp,
                        generatePushFeedBackProblemSign(discuss, grade, selected, timeStamp, language),
                        AdUtil.androidIdForApp,
                        grade,
                        selected,
                        discuss,
                        language
                    )
                Api.httpService.commitFeedBack(
                    newUserRequest
                )
            }
            pushFeedBackProblemObserver.postValue(resul)
        }
    }


    private fun generatePushFeedBackProblemSign(
        discuss: String,
        grade: String,
        selected: String,
        timestamp: Long,
        language: String
    ): String {
        val appId = Api.appID
        val appSecret = Api.appKey

        return EncryptUtil.getSHA256(
            appId +
                    appSecret +
                    discuss +
                    AdUtil.androidIdForApp +
                    grade +
                    UserManager.guestId +
                    language +
                    selected +
                    timestamp
        )
    }

    fun getFeedbackDefaultReasonConfig() {
        launchOnUI {
            val result = httpCall {
                val timeStamp = System.currentTimeMillis() / 1000
                val version = "laucher_feedback_default_reason"
                val request = RemoteConfigRequest(
                    Api.appID, version, timeStamp, RemoteConfig.generateRemoteConfigRequestSign(timeStamp, version)
                )
                Api.httpService.getFeedbackDefaultReason(request)
            }
            feedbackDefaultReasonConfigObserver.postValue(result)
        }
    }


}
