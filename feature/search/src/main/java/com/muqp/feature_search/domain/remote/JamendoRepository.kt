package com.muqp.feature_search.domain.remote

import androidx.paging.PagingSource
import com.muqp.feature_search.model.AlbumUI
import com.muqp.feature_search.model.ArtistUI
import com.muqp.feature_search.model.ItemAlbumUI
import com.muqp.feature_search.model.ItemArtistUI
import com.muqp.feature_search.model.ItemTrackUI
import com.muqp.feature_search.model.TrackUI

interface JamendoRepository {
    fun getSearchPagingResultToTrack(
        searchText: String?,
        order: String?,
        tags: String?
    ): PagingSource<Int, ItemTrackUI>

    fun getSearchPagingResultToAlbum(searchText: String): PagingSource<Int, ItemAlbumUI>

    fun getSearchPagingResultToArtist(searchText: String): PagingSource<Int, ItemArtistUI>

    suspend fun getSearchResultToTrack(searchText: String): TrackUI

    suspend fun getSearchResultToAlbum(searchText: String): AlbumUI

    suspend fun getSearchResultToArtist(searchText: String): ArtistUI
}