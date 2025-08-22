package com.muqp.feature_search.data.local

import com.muqp.core_database.database.dao.AlbumDao
import com.muqp.core_database.database.dao.ArtistDao
import com.muqp.core_database.database.dao.TrackDao
import com.muqp.feature_search.domain.local.FavoriteRepository
import com.muqp.feature_search.mapper.AlbumMapper.toItemAlbumEntity
import com.muqp.feature_search.mapper.AlbumMapper.toItemAlbumUI
import com.muqp.feature_search.mapper.ArtistMapper.toItemArtistEntity
import com.muqp.feature_search.mapper.ArtistMapper.toItemArtistUI
import com.muqp.feature_search.mapper.TrackMapper.toItemTrackEntity
import com.muqp.feature_search.mapper.TrackMapper.toItemTrackUI
import com.muqp.feature_search.model.ItemAlbumUI
import com.muqp.feature_search.model.ItemArtistUI
import com.muqp.feature_search.model.ItemTrackUI
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(
    private val trackDao: TrackDao,
    private val albumDao: AlbumDao,
    private val artistDao: ArtistDao
) : FavoriteRepository {
    override suspend fun getTrackById(trackId: String): ItemTrackUI? {
        return trackDao.getTrackById(trackId)?.toItemTrackUI()
    }

    override suspend fun insertTrack(itemTrackUI: ItemTrackUI) {
        trackDao.insertTrack(itemTrackUI.toItemTrackEntity())
    }

    override suspend fun deleteTrack(itemTrackUI: ItemTrackUI) {
        trackDao.deleteTrack(itemTrackUI.toItemTrackEntity())
    }

    override suspend fun getAlbumById(albumId: String): ItemAlbumUI? {
        return albumDao.getAlbumById(albumId)?.toItemAlbumUI()
    }

    override suspend fun insertAlbum(itemAlbumUI: ItemAlbumUI) {
        albumDao.insertAlbum(itemAlbumUI.toItemAlbumEntity())
    }

    override suspend fun deleteAlbum(itemAlbumUI: ItemAlbumUI) {
        albumDao.deleteAlbum(itemAlbumUI.toItemAlbumEntity())
    }

    override suspend fun getArtistById(artistId: String): ItemArtistUI? {
        return artistDao.getArtistById(artistId)?.toItemArtistUI()
    }

    override suspend fun insertArtist(itemArtistUI: ItemArtistUI) {
        artistDao.insertArtist(itemArtistUI.toItemArtistEntity())
    }

    override suspend fun deleteArtist(itemArtistUI: ItemArtistUI) {
        artistDao.deleteArtist(itemArtistUI.toItemArtistEntity())
    }
}