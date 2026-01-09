package com.hello.mihe.app.launcher.api

import com.android.launcher3.BuildConfig
import com.hello.mihe.app.launcher.api.models.AppGlobalConfigResponse
import com.hello.mihe.app.launcher.api.models.CommitFeedBackRequest
import com.hello.mihe.app.launcher.api.models.FeedbackDefaultReasonResponse
import com.hello.mihe.app.launcher.api.models.IpRecordRequest
import com.hello.mihe.app.launcher.api.models.IpRecordResponse
import com.hello.mihe.app.launcher.api.models.LoginRequest
import com.hello.mihe.app.launcher.api.models.RemoteConfigRequest
import com.hello.mihe.app.launcher.api.models.RootJson
import com.hello.mihe.app.launcher.utils.EncryptUtil
import com.hello.mihe.app.launcher.utils.EnvUtil
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.io.IOException
import java.util.concurrent.TimeUnit

object Api {
    private val BASE_URL = if (EnvUtil.isStaging) "https://miheappintl.staging2.p1staff.com/" else
        "https://miheappintl.tantanapp.com"

    private val logger =
        HttpLoggingInterceptor().apply {
            level =
                if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
        }
    private val okHttpClient: OkHttpClient =
        OkHttpClient.Builder().callTimeout(30, TimeUnit.SECONDS).addInterceptor(logger).build()

    val moshi: Moshi =
        Moshi.Builder()
            .add(Boolean::class.javaObjectType, BOOLEAN_JSON_ADAPTER)
            .add(Boolean::class.java, BOOLEAN_JSON_ADAPTER)
            .build()
    private val retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()
    val httpService: ApiService = retrofit.create(ApiService::class.java)
    val appID = if (EnvUtil.isStaging) {
        "dnj9dj12qic37mgv"
    } else {
        "ashs1ahlidhx9u4x"
    }

    val appKey =
        if (EnvUtil.isStaging) "^ocY^S2IwnQdSIwu1Ei&dOf6V*lyMkTq"
        else "ubpXu\$v4HbHCUCo19jMTpdwzL@tuc1Zk"

    fun sha256(str: String, time: Long): String = EncryptUtil.getSHA256(appID + appKey + str + time)

    inline fun <reified T> Moshi.listAdapter(): JsonAdapter<List<T>> {
        val type = Types.newParameterizedType(List::class.java, T::class.java)
        return adapter(type)
    }

    inline fun <reified T> Moshi.setAdapter(): JsonAdapter<Set<T>> {
        val type = Types.newParameterizedType(Set::class.java, T::class.java)
        return adapter(type)
    }

    inline fun <reified K, reified V> Moshi.mapAdapter(): JsonAdapter<Map<K, V>> {
        val type = Types.newParameterizedType(Map::class.java, K::class.java, V::class.java)
        return adapter(type)
    }

    inline fun <reified T> JsonAdapter<T>.fromJsonSafe(message: String): T? {
        return try {
            this.fromJson(message)
        } catch (th: Throwable) {
            if (BuildConfig.DEBUG) throw th
            null
        }
    }
}

interface ApiService {
    @POST("/hy/v1/user/activate")
    suspend fun activateUser(@Body body: LoginRequest): RootJson<Long>

    @POST("/hy/v1/ip/record")
    fun ipRecord(@Body body: IpRecordRequest): Call<RootJson<IpRecordResponse>>

    @POST("/v1/user/survey")
    suspend fun commitFeedBack(@Body body: CommitFeedBackRequest): RootJson<Boolean>

    @POST("/v1/config/info")
    suspend fun getFeedbackDefaultReason(@Body body: RemoteConfigRequest): RootJson<FeedbackDefaultReasonResponse>

    @POST("/v1/config/info")
    fun getAppGlobalConfig(@Body body: RemoteConfigRequest): Call<RootJson<AppGlobalConfigResponse>>
}

val BOOLEAN_JSON_ADAPTER: JsonAdapter<Boolean> =
    object : JsonAdapter<Boolean>() {
        @Throws(IOException::class)
        override fun fromJson(reader: JsonReader): Boolean {
            val type = reader.peek()
            return when (type) {
                JsonReader.Token.BOOLEAN -> reader.nextBoolean()
                JsonReader.Token.NUMBER -> reader.nextInt() != 0
                JsonReader.Token.STRING -> {
                    val value = reader.nextString()
                    value.equals("true", ignoreCase = true) || value == "1"
                }

                else -> {
                    throw IllegalArgumentException("Unexpected token: " + reader.peek())
                }
            }
        }

        @Throws(IOException::class)
        override fun toJson(writer: JsonWriter, value: Boolean?) {
            writer.value(value)
        }

        override fun toString(): String {
            return "JsonAdapter(Boolean)"
        }
    }
