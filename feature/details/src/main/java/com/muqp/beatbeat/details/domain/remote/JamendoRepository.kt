package com.muqp.beatbeat.details.domain.remote

import androidx.paging.PagingSource
import com.muqp.beatbeat.details.model.AlbumUI
import com.muqp.beatbeat.details.model.ItemTrackUI

interface JamendoRepository {
    suspend fun getAlbumDetails(albumId: Int): AlbumUI
    suspend fun getAllArtistAlbums(artistId: String): AlbumUI
    suspend fun getPopularArtistTracks(artistId: Int): List<ItemTrackUI>
    fun getPagingPopularArtistTracks(artistId: Int): PagingSource<Int, ItemTrackUI>
}