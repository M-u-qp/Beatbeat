package com.muqp.feature_listen.domain.local

import com.muqp.feature_listen.model.PlaylistUI

interface PlaylistRepository {
    suspend fun getAllPlaylists(): List<PlaylistUI>
    suspend fun addTrackToPlaylist(playlistId: Long, trackId: String)
}