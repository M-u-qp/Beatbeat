package com.muqp.feature_favorites.model

data class PlaylistTrackCrossRefUI(
    val playlistId: Long,
    val trackId: String,
    val position: Int,
    val addedAt: Long? = null
)
