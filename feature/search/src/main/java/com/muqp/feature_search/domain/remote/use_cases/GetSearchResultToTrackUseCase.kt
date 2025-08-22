package com.muqp.feature_search.domain.remote.use_cases

import androidx.paging.PagingSource
import com.muqp.feature_search.domain.remote.JamendoRepository
import com.muqp.feature_search.model.ItemTrackUI
import com.muqp.feature_search.model.TrackUI
import javax.inject.Inject

class GetSearchResultToTrackUseCase @Inject constructor(
    private val jamendoRepository: JamendoRepository
) {
    fun pagingInvoke(
        searchText: String? = null,
        order: String? = null,
        tags: String? = null
    ): PagingSource<Int, ItemTrackUI> {
        return jamendoRepository.getSearchPagingResultToTrack(searchText, order, tags)
    }

    suspend operator fun invoke(searchText: String): TrackUI {
        return jamendoRepository.getSearchResultToTrack(searchText)
    }
}