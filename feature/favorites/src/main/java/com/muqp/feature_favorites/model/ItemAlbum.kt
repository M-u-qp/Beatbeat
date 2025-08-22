package com.muqp.feature_favorites.model

data class ItemAlbum(
    val id: String,
    val name: String,
    val releaseDate: String,
    val artistId: String,
    val artistName: String,
    val image: String,
    val zip: String,
    val shareUrl: String,
    val zipAllowed: Boolean,
    val isFavorite: Boolean
)
