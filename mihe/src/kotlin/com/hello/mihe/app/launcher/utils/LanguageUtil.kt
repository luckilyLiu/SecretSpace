package com.hello.mihe.app.launcher.utils

import java.util.Locale

object LanguageUtil {

    private fun language(): String {
        val locale = Locale.getDefault()
        val language = if (locale == null) null else locale.language
        if (locale != null && language != null && "zh" == language) {
            return "zh"
        }
        return "en"
    }

    fun languageIsEn() = "en" == language()
    fun languageIsCn() = "zh" == language()
    @JvmField
    val MAX_LENGTH = 10

    @JvmStatic
    fun isChinese(c: Char): Boolean {
        return c.toInt() in 0x4E00..0x9FA5
    }

    /** 计算字符串的长度，中文加2，非中文加1 */
    @JvmStatic
    fun calcTextLength(charSequence: CharSequence?): Int {
        if (charSequence.isNullOrEmpty()) {
            return 0
        }

        var sum = 0
        for (c in charSequence) {
            sum += getCharTextCount(c)
        }
        return sum
    }

    /** 计算字符串除了[dstart,dend]外的字符数，中文加2，非中文加1 */
    @JvmStatic
    fun calcTextLength(source: CharSequence, dstart: Int, dend: Int): Int {
        var count = 0
        source.forEachIndexed { index, c ->
            if (dstart == dend || index !in dstart until dend) {
                count += getCharTextCount(c)
            }
        }
        return count
    }

    @JvmStatic
    fun getCharTextCount(c: Char) = if (isChinese(c)) 2 else 1

    /** 计算要删除的字符位置 */
    @JvmStatic
    fun getDeleteIndex(source: CharSequence, start: Int, end: Int, deleteCount: Int): Int {
        var sum = deleteCount
        for (index in end - 1 downTo start) {
            sum -= getCharTextCount(source[index])
            if (sum <= 0) {
                return index
            }
        }
        return 0
    }
}
