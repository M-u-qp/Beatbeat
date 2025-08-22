package com.muqp.feature_favorites.data

import com.muqp.core_database.database.dao.PlaylistDao
import com.muqp.core_database.database.dao.PlaylistTracksDao
import com.muqp.core_database.database.dao.TrackDao
import com.muqp.feature_favorites.domain.PlaylistRepository
import com.muqp.feature_favorites.mapper.PlaylistMapper.toPlaylistEntity
import com.muqp.feature_favorites.mapper.PlaylistMapper.toPlaylistTrackCrossRef
import com.muqp.feature_favorites.mapper.PlaylistMapper.toPlaylistUI
import com.muqp.feature_favorites.mapper.TrackMapper.toItemTrack
import com.muqp.feature_favorites.model.ItemTrack
import com.muqp.feature_favorites.model.PlaylistTrackCrossRefUI
import com.muqp.feature_favorites.model.PlaylistUI
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class PlaylistRepositoryImpl @Inject constructor(
    private val playlistDao: PlaylistDao,
    private val playlistTracksDao: PlaylistTracksDao,
    private val trackDao: TrackDao
) : PlaylistRepository {
    override suspend fun createPlaylist(
        name: String,
        description: String?,
        coverImage: String?
    ): Long {
        return playlistDao.insertPlaylist(
            PlaylistUI(
                name = name,
                description = description,
                coverImage = coverImage
            ).toPlaylistEntity()
        )
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

    override suspend fun getPlaylistWithTracks(playlistId: Long): Pair<PlaylistUI, List<ItemTrack>> {
        val playlist =
            playlistDao.getPlaylistById(playlistId) ?: throw Exception("Playlist not found")
        val crossRefs = playlistTracksDao.getTracksForPlaylist(playlistId)
        val tracks = crossRefs.mapNotNull { crossRef ->
            trackDao.getTrackById(crossRef.trackId)
        }
        return playlist.toPlaylistUI() to tracks.map { it.toItemTrack() }
    }

    override suspend fun getAllPlaylists(): List<PlaylistUI> {
        return playlistDao.getAllPlaylists().first().map { it.toPlaylistUI() }
    }

    override suspend fun getTrackCountForPlaylist(playlistId: Long): Int {
        return playlistTracksDao.getTrackCountForPlaylist(playlistId)
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        playlistDao.deletePlaylistById(playlistId)
    }

    override suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: String) {
        playlistTracksDao.removeTrackFromPlaylist(playlistId, trackId)
    }

    override suspend fun clearPlaylist(playlistId: Long) {
        playlistTracksDao.clearPlaylist(playlistId)
    }
}