package com.muqp.feature_search.data.remote

import androidx.paging.PagingSource
import com.muqp.core_network.api.JamendoApi
import com.muqp.core_ui.paging.CommonPagingSource
import com.muqp.core_ui.paging.PagingData
import com.muqp.beatbeat.search.BuildConfig
import com.muqp.feature_search.domain.remote.JamendoRepository
import com.muqp.feature_search.mapper.AlbumMapper.toAlbumUI
import com.muqp.feature_search.mapper.ArtistMapper.toArtistUI
import com.muqp.feature_search.mapper.TrackMapper.toTrackUI
import com.muqp.feature_search.model.AlbumUI
import com.muqp.feature_search.model.ArtistUI
import com.muqp.feature_search.model.ItemAlbumUI
import com.muqp.feature_search.model.ItemArtistUI
import com.muqp.feature_search.model.ItemTrackUI
import com.muqp.feature_search.model.TrackUI
import java.io.IOException
import javax.inject.Inject

class JamendoRepositoryImpl @Inject constructor(
    private val jamendoApi: JamendoApi
) : JamendoRepository {
    override fun getSearchPagingResultToTrack(
        searchText: String?,
        order: String?,
        tags: String?
    ): PagingSource<Int, ItemTrackUI> {
        return CommonPagingSource(
            loadData = { page ->
                val offset = (page - 1) * PAGE_SIZE_LONG
                val response = jamendoApi.getSearchResultToTrack(
                    clientId = BuildConfig.CLIENT_ID,
                    limit = PAGE_SIZE_LONG.toString(),
                    offset = offset,
                    search = searchText ?: "",
                    order = order ?: "",
                    tags = tags ?: ""
                )

                if (response.isSuccessful && response.body() != null) {
                    val searchResultResponse = response.body()!!.toTrackUI()
                    PagingData(
                        items = searchResultResponse.results,
                        total = searchResultResponse.headers.resultsCount.plus(offset)
                    )
                } else {
                    throw IOException("${response.code()} - ${response.message()}")
                }
            },
            getNextKey = { currentPage, totalItems ->
                if (totalItems < PAGE_SIZE_LONG) {
                    null
                } else {
                    currentPage + 1
                }
            }
        )
    }

    override fun getSearchPagingResultToAlbum(searchText: String): PagingSource<Int, ItemAlbumUI> {
        return CommonPagingSource(
            loadData = { page ->
                val offset = (page - 1) * PAGE_SIZE_LONG
                val response = jamendoApi.getSearchResultToAlbum(
                    clientId = BuildConfig.CLIENT_ID,
                    limit = PAGE_SIZE_LONG.toString(),
                    offset = offset,
                    nameSearch = searchText
                )

                if (response.isSuccessful && response.body() != null) {
                    val searchResultResponse = response.body()!!.toAlbumUI()
                    PagingData(
                        items = searchResultResponse.results,
                        total = searchResultResponse.headers.resultsCount.plus(offset)
                    )
                } else {
                    throw IOException("${response.code()} - ${response.message()}")
                }
            },
            getNextKey = { currentPage, totalItems ->
                if (totalItems < PAGE_SIZE_LONG) {
                    null
                } else {
                    currentPage + 1
                }
            }
        )
    }

    override fun getSearchPagingResultToArtist(searchText: String): PagingSource<Int, ItemArtistUI> {
        return CommonPagingSource(
            loadData = { page ->
                val offset = (page - 1) * PAGE_SIZE_LONG
                val response = jamendoApi.getSearchResultToArtist(
                    clientId = BuildConfig.CLIENT_ID,
                    limit = PAGE_SIZE_LONG.toString(),
                    offset = offset,
                    nameSearch = searchText
                )

                if (response.isSuccessful && response.body() != null) {
                    val searchResultResponse = response.body()!!.toArtistUI()
                    PagingData(
                        items = searchResultResponse.results,
                        total = searchResultResponse.headers.resultsCount.plus(offset)
                    )
                } else {
                    throw IOException("${response.code()} - ${response.message()}")
                }
            },
            getNextKey = { currentPage, totalItems ->
                if (totalItems < PAGE_SIZE_LONG) {
                    null
                } else {
                    currentPage + 1
                }
            }
        )
    }

    override suspend fun getSearchResultToTrack(searchText: String): TrackUI {
        return try {
            val response = jamendoApi.getSearchResultToTrack(
                clientId = BuildConfig.CLIENT_ID,
                limit = PAGE_SIZE_SHORT.toString(),
                offset = 0,
                search = searchText,
                order = null,
                tags = null
            )
            if (response.isSuccessful && response.body() != null) {
                response.body()!!.toTrackUI()
            } else {
                throw IOException("${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            throw IOException("${e.message}")
        }
    }

    override suspend fun getSearchResultToAlbum(searchText: String): AlbumUI {
        return try {
            val response = jamendoApi.getSearchResultToAlbum(
                clientId = BuildConfig.CLIENT_ID,
                limit = PAGE_SIZE_SHORT.toString(),
                offset = 0,
                nameSearch = searchText
            )
            if (response.isSuccessful && response.body() != null) {
                response.body()!!.toAlbumUI()
            } else {
                throw IOException("${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            throw IOException("${e.message}")
        }
    }

    override suspend fun getSearchResultToArtist(searchText: String): ArtistUI {
        return try {
            val response = jamendoApi.getSearchResultToArtist(
                clientId = BuildConfig.CLIENT_ID,
                limit = PAGE_SIZE_SHORT.toString(),
                offset = 0,
                nameSearch = searchText
            )
            if (response.isSuccessful && response.body() != null) {
                response.body()!!.toArtistUI()
            } else {
                throw IOException("${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            throw IOException("${e.message}")
        }
    }

    companion object {
        private const val PAGE_SIZE_LONG = 20
        private const val PAGE_SIZE_SHORT = 5
    }
}