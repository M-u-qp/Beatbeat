package com.muqp.feature_home.data

import androidx.paging.PagingSource
import com.muqp.core_network.api.JamendoApi
import com.muqp.core_ui.paging.CommonPagingSource
import com.muqp.core_ui.paging.PagingData
import com.muqp.beatbeat.home.BuildConfig
import com.muqp.feature_home.domain.JamendoRepository
import com.muqp.feature_home.mapper.MusicFeedsMapper.toMusicFeedsUi
import com.muqp.feature_home.model.FeedItemUI
import java.io.IOException
import javax.inject.Inject

class JamendoRepositoryImpl @Inject constructor(
    private val jamendoApi: JamendoApi
) : JamendoRepository {
    override fun getMusicFeeds(): PagingSource<Int, FeedItemUI> {
        return CommonPagingSource(
            loadData = { page ->
                try {
                    val offset = (page - 1) * PAGE_SIZE
                    val response = jamendoApi.getMusicFeeds(
                        clientId = BuildConfig.CLIENT_ID,
                        limit = PAGE_SIZE.toString(),
                        offset = offset
                    )

                    if (!response.isSuccessful || response.body() == null) {
                        throw IOException("${response.code()} - ${response.message()}")
                    }

                    val musicFeedsResponse = response.body()!!.toMusicFeedsUi()
                    PagingData(
                        items = musicFeedsResponse.results,
                        total = musicFeedsResponse.headers.resultsCount + offset
                    )

                } catch (e: IOException) {
                    throw IOException(e.message)
                }
            },
            getNextKey = { currentPage, totalItems ->
                if (totalItems < PAGE_SIZE) {
                    null
                } else {
                    currentPage + 1
                }
            }
        )
    }

    companion object {
        private const val PAGE_SIZE = 10
    }
}