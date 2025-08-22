package com.muqp.core_utils.extensions

import java.net.URI
import java.net.URISyntaxException

object StringExt {
    fun String.toHttps(): String {
        return if (isValidUrl()) {
            if (this.startsWith("http://")) {
                this.replace("http://", "https://")
            } else {
                this
            }
        } else {
            this
        }
    }

    private fun String.isValidUrl(): Boolean {
        return try {
            URI(this).toURL()
            true
        } catch (e: URISyntaxException) {
            false
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}