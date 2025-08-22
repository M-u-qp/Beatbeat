package com.muqp.feature_search.domain.local

import com.muqp.feature_search.model.PlaylistUI

interface PlaylistRepository {
    suspend fun getAllPlaylists(): List<PlaylistUI>
    suspend fun addTrackToPlaylist(playlistId: Long, trackId: String)
}