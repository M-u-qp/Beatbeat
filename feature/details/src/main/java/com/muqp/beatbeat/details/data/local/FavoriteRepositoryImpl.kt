package com.muqp.beatbeat.details.data.local

import com.muqp.beatbeat.details.domain.local.FavoriteRepository
import com.muqp.beatbeat.details.mapper.AlbumMapper.toItemAlbumEntity
import com.muqp.beatbeat.details.mapper.AlbumMapper.toItemAlbumUI
import com.muqp.beatbeat.details.mapper.ArtistMapper.toArtistUI
import com.muqp.beatbeat.details.mapper.ArtistMapper.toItemArtistEntity
import com.muqp.beatbeat.details.mapper.TrackMapper.toItemTrackEntity
import com.muqp.beatbeat.details.mapper.TrackMapper.toItemTrackUI
import com.muqp.beatbeat.details.model.ItemArtistUI
import com.muqp.beatbeat.details.model.ItemAlbumUI
import com.muqp.beatbeat.details.model.ItemTrackUI
import com.muqp.core_database.database.dao.AlbumDao
import com.muqp.core_database.database.dao.ArtistDao
import com.muqp.core_database.database.dao.TrackDao
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
        return artistDao.getArtistById(artistId)?.toArtistUI()
    }

    override suspend fun insertArtist(itemArtistUI: ItemArtistUI) {
        artistDao.insertArtist(itemArtistUI.toItemArtistEntity())
    }

    override suspend fun deleteArtist(itemArtistUI: ItemArtistUI) {
        artistDao.deleteArtist(itemArtistUI.toItemArtistEntity())
    }
}