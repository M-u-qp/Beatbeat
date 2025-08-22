package com.muqp.feature_search.model

data class AlbumUI(
    val headers: HeadersAlbumUI,
    val results: List<ItemAlbumUI>
)

data class HeadersAlbumUI(
    val resultsCount: Int
)

data class ItemAlbumUI(
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