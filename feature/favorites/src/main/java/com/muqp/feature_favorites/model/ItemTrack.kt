package com.muqp.feature_favorites.model

data class ItemTrack(
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
