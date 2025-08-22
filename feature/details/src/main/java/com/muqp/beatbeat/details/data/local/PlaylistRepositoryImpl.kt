package com.muqp.beatbeat.details.data.local

import com.muqp.beatbeat.details.domain.local.PlaylistRepository
import com.muqp.beatbeat.details.mapper.PlaylistMapper.toPlaylistTrackCrossRef
import com.muqp.beatbeat.details.mapper.PlaylistMapper.toPlaylistUI
import com.muqp.beatbeat.details.model.PlaylistTrackCrossRefUI
import com.muqp.beatbeat.details.model.PlaylistUI
import com.muqp.core_database.database.dao.PlaylistDao
import com.muqp.core_database.database.dao.PlaylistTracksDao
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