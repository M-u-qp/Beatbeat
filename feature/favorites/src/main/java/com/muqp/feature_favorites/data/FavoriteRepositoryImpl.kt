package com.muqp.feature_favorites.data

import com.muqp.core_database.database.dao.AlbumDao
import com.muqp.core_database.database.dao.ArtistDao
import com.muqp.core_database.database.dao.TrackDao
import com.muqp.feature_favorites.domain.FavoriteRepository
import com.muqp.feature_favorites.mapper.AlbumMapper.toItemAlbum
import com.muqp.feature_favorites.mapper.AlbumMapper.toItemAlbumEntity
import com.muqp.feature_favorites.mapper.ArtistMapper.toItemArtist
import com.muqp.feature_favorites.mapper.ArtistMapper.toItemArtistEntity
import com.muqp.feature_favorites.mapper.TrackMapper.toItemTrack
import com.muqp.feature_favorites.mapper.TrackMapper.toItemTrackEntity
import com.muqp.feature_favorites.model.ItemAlbum
import com.muqp.feature_favorites.model.ItemArtist
import com.muqp.feature_favorites.model.ItemTrack
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(
    private val trackDao: TrackDao,
    private val albumDao: AlbumDao,
    private val artistDao: ArtistDao
) : FavoriteRepository {
    override suspend fun getTrackById(trackId: String): ItemTrack? {
        return trackDao.getTrackById(trackId)?.toItemTrack()
    }

    override suspend fun deleteTrack(itemTrack: ItemTrack) {
        trackDao.deleteTrack(itemTrack.toItemTrackEntity())
    }

    override fun getAllTracks(): Flow<List<ItemTrack>> {
        return trackDao.getAllTracks()
            .map { it.map { itemTrackEntity -> itemTrackEntity.toItemTrack() } }
    }

    override suspend fun getAlbumById(albumId: String): ItemAlbum? {
        return albumDao.getAlbumById(albumId)?.toItemAlbum()
    }

    override suspend fun deleteAlbum(itemAlbum: ItemAlbum) {
        albumDao.deleteAlbum(itemAlbum.toItemAlbumEntity())
    }

    override fun getAllAlbums(): Flow<List<ItemAlbum>> {
        return albumDao.getAllAlbums()
            .map { it.map { itemAlbumEntity -> itemAlbumEntity.toItemAlbum() } }
    }

    override suspend fun getArtistById(artistId: String): ItemArtist? {
        return artistDao.getArtistById(artistId)?.toItemArtist()
    }

    override suspend fun deleteArtist(itemArtist: ItemArtist) {
        artistDao.deleteArtist(itemArtist.toItemArtistEntity())
    }

    override fun getAllArtists(): Flow<List<ItemArtist>> {
        return artistDao.getAllArtists()
            .map { it.map { itemArtistsEntity -> itemArtistsEntity.toItemArtist() } }
    }
}