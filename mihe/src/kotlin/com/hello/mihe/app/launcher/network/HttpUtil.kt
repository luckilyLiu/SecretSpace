package com.hello.mihe.app.launcher.network

import android.os.SystemClock
import com.hello.mihe.app.launcher.network.ExceptionChecker
import com.hello.mihe.app.launcher.network.gson.GsonUtils
import java.text.ParseException
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.Headers.Companion.toHeaders
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject

object HttpUtil {
  @Volatile private var lastElapsedRealtime: Long = -1
  @Volatile private var lastServerTime: Long = -1
  /** 同步的post请求 body: 会自动使用Gson把body序列化为String */
  @Throws(Exception::class)
  fun post(
    okHttpClient: OkHttpClient,
    urlString: String,
    body: Any?,
    headers: MutableMap<String, String>?
  ): String {
    val requestBuilder = Request.Builder().url(urlString)
    val json = if (body != null) GsonUtils.toJson(body) else ""
    val requestBody =
      RequestBody.create("application/json;charset=utf-8".toMediaTypeOrNull(), json.toString())
    requestBuilder.post(requestBody)
    headers?.let { requestBuilder.headers(it.toHeaders()) }
    var res: String? = null
    try {
      val response = okHttpClient.newCall(requestBuilder.build()).execute()
      val exception = ExceptionChecker.check(response)
      if (exception != null) {
        throw exception
      }
      updateServerTime(response)
      res = response.body?.string()
    } catch (e: Exception) {
      throw e
    }
    return res ?: ""
  }

  @Throws(Exception::class)
  fun patch(
    okHttpClient: OkHttpClient,
    urlString: String,
    body: Any?,
    headers: MutableMap<String, String>?
  ): String {
    val requestBuilder = Request.Builder().url(urlString)
    val json = if (body != null) GsonUtils.toJson(body) else ""
    val requestBody =
      RequestBody.create("application/json;charset=utf-8".toMediaTypeOrNull(), json.toString())
    requestBuilder.patch(requestBody)
    headers?.let { requestBuilder.headers(it.toHeaders()) }
    var res: String? = null
    try {
      val response = okHttpClient.newCall(requestBuilder.build()).execute()
      val exception = ExceptionChecker.check(response)
      if (exception != null) {
        throw exception
      }
      updateServerTime(response)
      res = response.body?.string()
    } catch (e: Exception) {
      throw e
    }
    return res ?: ""
  }

  @Throws(Exception::class)
  fun post(
    okHttpClient: OkHttpClient,
    urlString: String,
    body: JSONObject?,
    headers: MutableMap<String, String>?
  ): String {
    val requestBuilder = Request.Builder().url(urlString)
    val JSON = "application/json;charset=utf-8".toMediaTypeOrNull()
    val requestBody = RequestBody.create(JSON, body.toString())
    requestBuilder.post(requestBody)
    headers?.let { requestBuilder.headers(it.toHeaders()) }
    var res: String? = null
    try {
      val response = okHttpClient.newCall(requestBuilder.build()).execute()
      val exception = ExceptionChecker.check(response)
      if (exception != null) {
        throw exception
      }
      updateServerTime(response)
      res = response.body?.string()
    } catch (e: Exception) {
      throw e
    }
    return res ?: ""
  }

  @Throws(Exception::class)
  fun get(
    okHttpClient: OkHttpClient,
    urlString: String,
    headers: MutableMap<String, String>?
  ): String {
    val requestBuilder = Request.Builder().url(urlString)
    requestBuilder.get()
    headers?.let { requestBuilder.headers(it.toHeaders()) }
    var res: String? = null
    try {
      val response = okHttpClient.newCall(requestBuilder.build()).execute()
      val exception = ExceptionChecker.check(response)
      if (exception != null) {
        throw exception
      }
      updateServerTime(response)
      res = response.body?.string()
    } catch (e: Exception) {
      throw e
    }
    return res ?: ""
  }

  /** 异步post请求 body: 需要@Keep，会自动使用Gson把body序列化为String success & error 均回调在子线程！！！ */
  fun postWithCallback(
    okHttpClient: OkHttpClient,
    urlString: String,
    body: Any?,
    headers: MutableMap<String, String>?,
    success: (str: String) -> Unit,
    error: ((exception: Exception) -> Unit)? = null
  ) {
    GlobalScope.launch(Dispatchers.Default + CoroutineName("http")) {
      try {
        val result = post(okHttpClient, urlString, body, headers)
        success.invoke(result)
      } catch (e: Exception) {
        error?.invoke(e)
      }
    }
  }

  /** 异步post请求 body: 需要@Keep，会自动使用Gson把body序列化为String success & error 均回调在子线程！！！ */
  fun patchWithCallback(
    okHttpClient: OkHttpClient,
    urlString: String,
    body: Any?,
    headers: MutableMap<String, String>?,
    success: (str: String) -> Unit,
    error: ((exception: Exception) -> Unit)? = null
  ) {
    GlobalScope.launch(Dispatchers.Default + CoroutineName("http")) {
      try {
        val result = patch(okHttpClient, urlString, body, headers)
        success.invoke(result)
      } catch (e: Exception) {
        error?.invoke(e)
      }
    }
  }

  fun guessedCurrentServerTime(): Long {
    val time: Long
    if (lastServerTime > 0) {
      time = lastServerTime + ((SystemClock.elapsedRealtime() - lastElapsedRealtime)) / 1000L
      if (time > 0) return time
    }
    return System.currentTimeMillis() / 1000L
  }

  private fun updateServerTime(response: Response?) {
    if (response == null) {
      return
    }
    // 服务器时间戳
    val headerTime = response.header("X-Timestamp")
    if (lastServerTime == -1L && headerTime != null) {
      try {
        lastElapsedRealtime = SystemClock.elapsedRealtime()
        lastServerTime = headerTime.toLong()
      } catch (e: ParseException) {
        // 防止解析时间报错影响数据解析
      }
    }
  }
}
