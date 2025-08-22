package com.muqp.feature_favorites.domain

import com.muqp.feature_favorites.model.ItemAlbum
import com.muqp.feature_favorites.model.ItemArtist
import com.muqp.feature_favorites.model.ItemTrack
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    suspend fun getTrackById(trackId: String): ItemTrack?
    suspend fun deleteTrack(itemTrack: ItemTrack)
    fun getAllTracks(): Flow<List<ItemTrack>>

    suspend fun getAlbumById(albumId: String): ItemAlbum?
    suspend fun deleteAlbum(itemAlbum: ItemAlbum)
    fun getAllAlbums(): Flow<List<ItemAlbum>>

    suspend fun getArtistById(artistId: String): ItemArtist?
    suspend fun deleteArtist(itemArtist: ItemArtist)
    fun getAllArtists(): Flow<List<ItemArtist>>
}