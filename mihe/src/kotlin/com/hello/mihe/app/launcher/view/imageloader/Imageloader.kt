package com.hello.mihe.app.launcher.view.imageloader

import android.content.Context
import app.lawnchair.LawnchairApp
import coil.Coil
import coil.ImageLoader
import coil.decode.DataSource
import coil.fetch.DrawableResult
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.request.Options


class ApkFetcher(val apkPath: String) : Fetcher {
    override suspend fun fetch(): FetchResult? {
        val drawable = kotlin.runCatching {
            val packageManager = LawnchairApp.instance.packageManager
            val applicationInfo = packageManager.getPackageArchiveInfo(apkPath, 0)?.applicationInfo
            applicationInfo?.publicSourceDir = apkPath
            applicationInfo?.sourceDir = apkPath

            try {
                applicationInfo?.loadUnbadgedIcon(packageManager)
            } catch (th: Throwable) {
                applicationInfo?.loadIcon(packageManager)
            }
        }.getOrNull() ?: return null
        return DrawableResult(drawable, false, DataSource.MEMORY)
    }
}

class ApkFetcherFactory : Fetcher.Factory<String> {
    override fun create(data: String, options: Options, imageLoader: ImageLoader): Fetcher? {
        return ApkFetcher(data)
    }
}

fun initCoil(context: Context) {
    val loader = ImageLoader.Builder(context)
        .components {
            add(ApkFetcherFactory())
        }.build()
    Coil.setImageLoader(loader)
}