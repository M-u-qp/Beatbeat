package com.muqp.core_utils.extensions

import android.content.Context
import android.content.Intent
import android.net.Uri

object ContextExt {
    fun Context.openInBrowser(url: String) {
        Intent(Intent.ACTION_VIEW).also {
            it.data = Uri.parse(url)
            if (it.resolveActivity(packageManager) != null) {
                startActivity(it)
            }
        }
    }

    fun Context.shareUrl(url: String, title: String = "") {
        Intent(Intent.ACTION_SEND).also {
            it.putExtra(Intent.EXTRA_TEXT, url)
            it.type = "text/plain"
            if (it.resolveActivity(packageManager) != null) {
                startActivity(Intent.createChooser(it, title))
            }
        }
    }
}