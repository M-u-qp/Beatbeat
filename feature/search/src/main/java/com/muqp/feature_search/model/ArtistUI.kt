package com.muqp.feature_search.model

data class ArtistUI(
    val headers: HeadersArtistUI,
    val results: List<ItemArtistUI>
)

data class HeadersArtistUI(
    val resultsCount: Int
)

data class ItemArtistUI(
    val id: String,
    val name: String,
    val website: String,
    val joinDate: String,
    val image: String,
    val shareUrl: String,
    val isFavorite: Boolean
)