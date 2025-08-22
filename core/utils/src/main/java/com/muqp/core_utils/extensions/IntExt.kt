package com.muqp.core_utils.extensions

import android.content.Context

object IntExt {
    fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }

    fun Int.getPluralForm(
        one: String,
        few: String,
        many: String
    ): String {
        return when {
            this % 10 == 1 && this % 100 != 11 -> "$this $one"
            this % 10 in 2..4 && this % 100 !in 12..14 -> "$this $few"
            else -> "$this $many"
        }
    }
}