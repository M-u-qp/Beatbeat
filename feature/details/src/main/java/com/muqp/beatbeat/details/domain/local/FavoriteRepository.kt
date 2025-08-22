package com.muqp.beatbeat.details.domain.local

import com.muqp.beatbeat.details.model.ItemArtistUI
import com.muqp.beatbeat.details.model.ItemAlbumUI
import com.muqp.beatbeat.details.model.ItemTrackUI

interface FavoriteRepository {
    suspend fun getTrackById(trackId: String): ItemTrackUI?
    suspend fun insertTrack(itemTrackUI: ItemTrackUI)
    suspend fun deleteTrack(itemTrackUI: ItemTrackUI)

    suspend fun getAlbumById(albumId: String): ItemAlbumUI?
    suspend fun insertAlbum(itemAlbumUI: ItemAlbumUI)
    suspend fun deleteAlbum(itemAlbumUI: ItemAlbumUI)

    suspend fun getArtistById(artistId: String): ItemArtistUI?
    suspend fun insertArtist(itemArtistUI: ItemArtistUI)
    suspend fun deleteArtist(itemArtistUI: ItemArtistUI)
}