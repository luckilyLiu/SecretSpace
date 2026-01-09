package com.hello.mihe.app.launcher.utils

import java.util.Calendar

object TimeHelper {
    fun isSameDay(curTime: Long, time: Long): Boolean {
        val calendar1 = Calendar.getInstance()
        calendar1.timeInMillis = time
        val calendar2 = Calendar.getInstance()
        calendar2.timeInMillis = curTime
        return calendar1[Calendar.DAY_OF_MONTH] == calendar2[Calendar.DAY_OF_MONTH] &&
                calendar1[Calendar.MONTH] == calendar2[Calendar.MONTH] &&
                calendar1[Calendar.YEAR] == calendar2[Calendar.YEAR]
    }
}
