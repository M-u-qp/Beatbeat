package com.muqp.core_network.model

data class AllArtistAlbumsResponse(
    val results: List<ItemAlbumWithoutTracksResponse>
)

data class ItemAlbumWithoutTracksResponse(
    val id: String,
    val name: String,
    val releasedate: String,
    val artist_id: String,
    val artist_name: String,
    val image: String,
    val shareurl: String
)
