package com.muqp.core_network.model

data class AlbumDetailsResponse(
    val results: List<ItemAlbumResponse>
)

data class ItemAlbumResponse(
    val id: String,
    val name: String,
    val releasedate: String,
    val artist_id: String,
    val artist_name: String,
    val image: String,
    val tracks: List<ItemTracksResponse>
)

data class ItemTracksResponse(
    val id: String,
    val name: String,
    val duration: String,
    val audio: String,
    val audiodownload: String,
    val audiodownload_allowed: Boolean
)
