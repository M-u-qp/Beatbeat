package com.muqp.beatbeat.details.data.remote

import androidx.paging.PagingSource
import com.muqp.beatbeat.details.BuildConfig
import com.muqp.beatbeat.details.domain.remote.JamendoRepository
import com.muqp.beatbeat.details.mapper.AlbumMapper.toAlbumUI
import com.muqp.beatbeat.details.mapper.TrackMapper.toItemTrackUI
import com.muqp.beatbeat.details.model.AlbumUI
import com.muqp.beatbeat.details.model.ItemTrackUI
import com.muqp.core_network.api.JamendoApi
import com.muqp.core_network.model.AlbumDetailsResponse
import com.muqp.core_network.model.AllArtistAlbumsResponse
import com.muqp.core_network.model.SearchResultToTrackResponse
import com.muqp.core_ui.paging.CommonPagingSource
import com.muqp.core_ui.paging.PagingData
import com.muqp.core_utils.state.AppException
import com.muqp.core_utils.state.wrapNetworkCall
import retrofit2.Response
import javax.inject.Inject

class JamendoRepositoryImpl @Inject constructor(
    private val jamendoApi: JamendoApi
) : JamendoRepository {
    override suspend fun getAlbumDetails(albumId: Int): AlbumUI = wrapNetworkCall {
        jamendoApi.getAlbumDetails(
            clientId = BuildConfig.CLIENT_ID,
            id = albumId
        ).toAlbumUIOrThrow()
    }

    override suspend fun getAllArtistAlbums(artistId: String): AlbumUI = wrapNetworkCall {
        jamendoApi.getAllArtistAlbums(
            clientId = BuildConfig.CLIENT_ID,
            artistId = artistId
        ).toAlbumUIOrThrow2()
    }

    override suspend fun getPopularArtistTracks(artistId: Int): List<ItemTrackUI> =
        wrapNetworkCall {
            jamendoApi.getSearchResultToTrack(
                clientId = BuildConfig.CLIENT_ID,
                artistId = artistId,
                order = POPULARITY_TOTAL
            ).toTrackListOrThrow()
        }

    override fun getPagingPopularArtistTracks(artistId: Int): PagingSource<Int, ItemTrackUI> {
        return CommonPagingSource(
            loadData = { page ->
                wrapNetworkCall {
                    val offset = (page - 1) * PAGE_SIZE
                    val response = jamendoApi.getSearchResultToTrack(
                        clientId = BuildConfig.CLIENT_ID,
                        limit = PAGE_SIZE.toString(),
                        offset = offset,
                        artistId = artistId,
                        order = POPULARITY_TOTAL
                    )

                    val items = response.toTrackListOrThrow()
                    PagingData(
                        items = items,
                        total = PAGE_SIZE.plus(offset)
                    )
                }
            },
            getNextKey = { currentPage, totalItems ->
                if (totalItems < PAGE_SIZE) null else currentPage + 1
            }
        )
    }

    private fun Response<AlbumDetailsResponse>.toAlbumUIOrThrow(): AlbumUI {
        if (!isSuccessful || body() == null) {
            throw AppException.NetworkException(code = code(), message = message())
        }
        return body()!!.toAlbumUI()
    }

    private fun Response<AllArtistAlbumsResponse>.toAlbumUIOrThrow2(): AlbumUI {
        if (!isSuccessful || body() == null) {
            throw AppException.NetworkException(code = code(), message = message())
        }
        return body()!!.toAlbumUI()
    }

    private fun Response<SearchResultToTrackResponse>.toTrackListOrThrow(): List<ItemTrackUI> {
        if (!isSuccessful || body() == null) {
            throw AppException.NetworkException(code = code(), message = message())
        }
        return body()!!.results.map { it.toItemTrackUI() }
    }

    companion object {
        private const val PAGE_SIZE = 20
        private const val POPULARITY_TOTAL = "popularity_total"
    }
}