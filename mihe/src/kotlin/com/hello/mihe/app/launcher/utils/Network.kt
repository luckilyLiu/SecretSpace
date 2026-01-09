package com.hello.mihe.app.launcher.utils

import com.android.launcher3.BuildConfig
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class Network {

  companion object {
    var httpBuilder: OkHttpClient.Builder =
      OkHttpClient().newBuilder().apply {
        connectTimeout(60, TimeUnit.SECONDS)
        readTimeout(60, TimeUnit.SECONDS)
        if (BuildConfig.DEBUG) {
          val logging = HttpLoggingInterceptor()
          logging.level = HttpLoggingInterceptor.Level.BODY
          addInterceptor(logging)
        }
      }

    private val okHttpClient: OkHttpClient = httpBuilder.build()
    fun getOkHttpClient(): OkHttpClient {
      return okHttpClient
    }
  }
}
