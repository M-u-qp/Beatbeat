package com.muqp.beatbeat.details.model

data class ItemTrackUI(
    val id: String,
    val name: String,
    val duration: String,
    val audio: String,
    val audioDownload: String,
    val audioDownloadAllowed: Boolean,
    val isFavorite: Boolean
)
