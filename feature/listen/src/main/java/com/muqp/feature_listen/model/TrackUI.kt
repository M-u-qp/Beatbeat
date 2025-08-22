package com.muqp.feature_listen.model

data class TrackUI(
    val results: List<ItemTrackUI>
)

data class ItemTrackUI(
    val id: String,
    val name: String,
    val duration: Int,
    val artistId: String,
    val artistName: String,
    val albumName: String,
    val albumId: String,
    val releaseDate: String,
    val albumImage: String,
    val audio: String,
    val audioDownload: String,
    val shareUrl: String,
    val image: String,
    val audioDownloadAllowed: Boolean,
    val isFavorite: Boolean
)
