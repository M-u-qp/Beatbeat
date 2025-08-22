package com.muqp.beatbeat.details.domain.local

import com.muqp.beatbeat.details.model.PlaylistUI

interface PlaylistRepository {
    suspend fun getAllPlaylists(): List<PlaylistUI>
    suspend fun addTrackToPlaylist(playlistId: Long, trackId: String)
}