package com.hello.mihe.app.launcher.ui.base

import androidx.lifecycle.MutableLiveData
import com.hello.mihe.app.launcher.api.models.BaseResponse

class StateLiveData<T> : MutableLiveData<BaseResponse<T>>()
