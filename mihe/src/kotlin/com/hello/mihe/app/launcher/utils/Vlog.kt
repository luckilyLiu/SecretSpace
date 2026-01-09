package com.hello.mihe.app.launcher.utils

import android.util.Log

object Vlog {
    @JvmStatic
    var enableLog = false

    @JvmStatic
    fun v(tag: String, msg: String) {
        if (!enableLog) return
        Log.v(tag, msg)
    }

    @JvmStatic
    inline fun v(tag: String, msg: () -> String) {
        if (!enableLog) return
        Log.v(tag, msg())
    }

    @JvmStatic
    inline fun v(tag: String, tr: Throwable, msg: () -> String) {
        if (!enableLog) return
        Log.v(tag, msg(), tr)
    }

    @JvmStatic
    inline fun v(tag: String, tr: Throwable, msg: String) {
        if (!enableLog) return
        Log.v(tag, msg, tr)
    }

    @JvmStatic
    inline fun d(tag: String, msg: () -> String) {
        if (!enableLog) return
        Log.d(tag, msg())
    }

    @JvmStatic
    inline fun d(tag: String, tr: Throwable, msg: () -> String) {
        if (!enableLog) return
        Log.d(tag, msg(), tr)
    }

    @JvmStatic
    inline fun i(tag: String, msg: () -> String) {
        if (!enableLog) return
        Log.i(tag, msg())
    }

    @JvmStatic
    fun i(tag: String, msg: String) {
        if (!enableLog) return
        Log.i(tag, msg)
    }

    @JvmStatic
    inline fun i(tag: String, tr: Throwable, msg: () -> String) {
        if (!enableLog) return
        Log.i(tag, msg(), tr)
    }

    @JvmStatic
    fun w(tag: String, msg: String) {
        if (!enableLog) return
        Log.w(tag, msg)
    }

    @JvmStatic
    inline fun w(tag: String, msg: () -> String) {
        if (!enableLog) return
        Log.w(tag, msg())
    }

    @JvmStatic
    inline fun w(tag: String, tr: Throwable, msg: () -> String) {
        if (!enableLog) return
        Log.w(tag, msg(), tr)
    }

    @JvmStatic
    fun w(tag: String, tr: Throwable) {
        if (!enableLog) return
        Log.w(tag, tr)
    }

    @JvmStatic
    inline fun e(tag: String, msg: () -> String) {
        if (!enableLog) return
        Log.e(tag, msg())
    }

    @JvmStatic
    inline fun e(tag: String, msg: String) {
        if (!enableLog) return
        Log.e(tag, msg)
    }

    @JvmStatic
    inline fun e(tag: String, tr: Throwable, msg: () -> String) {
        if (!enableLog) return
        Log.e(tag, msg(), tr)
    }

    @JvmStatic
    inline fun e(tag: String, tr: Throwable, msg: String) {
        if (!enableLog) return
        Log.e(tag, msg, tr)
    }

    @JvmStatic
    inline fun d(tag: String, msg: String) {
        if (!enableLog) return
        Log.d(tag, msg)
    }
}
