package com.hello.mihe.app.launcher.network.exception

import androidx.annotation.Keep

@Keep data class ErrorMsg(var meta: ErrorMeta?)

@Keep data class ErrorMeta(var code: Int, var message: String)
