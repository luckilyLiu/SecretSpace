package com.hello.mihe.app.launcher.ui.base

import android.content.Context
import android.content.pm.LauncherApps
import android.os.Handler
import android.os.UserManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lawnchair.util.App
import app.lawnchair.util.appComparator
import com.android.launcher3.AppFilter
import com.android.launcher3.Utilities
import com.android.launcher3.pm.UserCache
import com.android.launcher3.util.Executors.MODEL_EXECUTOR
import com.hello.mihe.app.launcher.api.models.BaseResponse
import com.hello.mihe.app.launcher.api.models.RootJson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

abstract class BaseViewModel : ViewModel() {
    fun launchOnUI(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    block()
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }
    }

    inline fun <T> httpCall(block: () -> RootJson<T>): BaseResponse<T> {
        val baseResponse = BaseResponse<T>()
        try {
            block().apply {
                baseResponse.data = this.data
                baseResponse.meta = this.meta
            }
        } catch (th: Throwable) {
            baseResponse.error = th
        }
        return baseResponse
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

    protected suspend fun loadApp(context: Context): List<App> = suspendCoroutine { continuation ->
        Utilities.postAsyncCallback(Handler(MODEL_EXECUTOR.looper)) {
            val launcherApps = context.getSystemService(LauncherApps::class.java)
            val appFilter = AppFilter(context)
            var userHandles = UserCache.INSTANCE.get(context).userProfiles
            if (userHandles.isEmpty()) {
                userHandles = UserCache.queryAllUsers(context.getSystemService(UserManager::class.java)).map { it.key }
            }
            val result = userHandles
                .flatMap { launcherApps.getActivityList(null, it) }
                .filter { appFilter.shouldShowApp(it.componentName) }
                .map { App(context, it) }
                .sortedWith(appComparator)
                .toMutableList()
            continuation.resume(result)
        }
    }
}
