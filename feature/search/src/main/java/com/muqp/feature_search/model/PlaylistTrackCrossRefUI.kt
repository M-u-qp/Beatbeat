package com.muqp.feature_search.model

data class PlaylistTrackCrossRefUI(
    val playlistId: Long,
    val trackId: String,
    val position: Int,
    val addedAt: Long? = null
)
