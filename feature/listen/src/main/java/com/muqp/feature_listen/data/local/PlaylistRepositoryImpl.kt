package com.muqp.feature_listen.data.local

import com.muqp.core_database.database.dao.PlaylistDao
import com.muqp.core_database.database.dao.PlaylistTracksDao
import com.muqp.feature_listen.domain.local.PlaylistRepository
import com.muqp.feature_listen.mapper.PlaylistMapper.toPlaylistTrackCrossRef
import com.muqp.feature_listen.mapper.PlaylistMapper.toPlaylistUI
import com.muqp.feature_listen.model.PlaylistTrackCrossRefUI
import com.muqp.feature_listen.model.PlaylistUI
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class PlaylistRepositoryImpl @Inject constructor(
    private val playlistDao: PlaylistDao,
    private val playlistTracksDao: PlaylistTracksDao
): PlaylistRepository {
    override suspend fun getAllPlaylists(): List<PlaylistUI> {
        return playlistDao.getAllPlaylists().first().map { it.toPlaylistUI() }
    }

    override suspend fun addTrackToPlaylist(playlistId: Long, trackId: String) {
        playlistTracksDao.addTrackToPlaylist(
            PlaylistTrackCrossRefUI(
                playlistId = playlistId,
                trackId = trackId,
                position = playlistTracksDao.getTrackCountForPlaylist(playlistId)
            ).toPlaylistTrackCrossRef()
        )
    }
}