package com.muqp.feature_favorites.domain

import com.muqp.feature_favorites.model.ItemTrack
import com.muqp.feature_favorites.model.PlaylistUI

interface PlaylistRepository {
    suspend fun createPlaylist(
        name: String,
        description: String?,
        coverImage: String?
    ): Long
    suspend fun addTrackToPlaylist(playlistId: Long, trackId: String)
    suspend fun getPlaylistWithTracks(playlistId: Long): Pair<PlaylistUI, List<ItemTrack>>
    suspend fun getAllPlaylists(): List<PlaylistUI>
    suspend fun getTrackCountForPlaylist(playlistId: Long): Int
    suspend fun deletePlaylist(playlistId: Long)
    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: String)
    suspend fun clearPlaylist(playlistId: Long)
}