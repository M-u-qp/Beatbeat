package com.muqp.core_ui.common

import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat

object HideKeyboard {
    fun hideKeyboard(view: View) {
        val imm = ContextCompat.getSystemService(view.context, InputMethodManager::class.java)
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }
}