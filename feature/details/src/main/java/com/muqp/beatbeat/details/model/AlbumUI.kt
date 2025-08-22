package com.muqp.beatbeat.details.model

data class AlbumUI(
    val results: List<ItemAlbumUI>
)

data class ItemAlbumUI(
    val id: String,
    val name: String,
    val releaseDate: String,
    val artistId: String,
    val artistName: String,
    val image: String,
    val tracks: List<ItemTrackUI>,
    val isFavorite: Boolean
)