package com.muqp.feature_favorites.model

data class ItemArtist(
    val id: String,
    val name: String,
    val website: String,
    val joinDate: String,
    val image: String,
    val shareUrl: String,
    val isFavorite: Boolean
)
