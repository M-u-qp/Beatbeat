package com.muqp.beatbeat.details.model

data class PlaylistTrackCrossRefUI(
    val playlistId: Long,
    val trackId: String,
    val position: Int,
    val addedAt: Long? = null
)
