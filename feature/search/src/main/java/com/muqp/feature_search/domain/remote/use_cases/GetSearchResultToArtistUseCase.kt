package com.muqp.feature_search.domain.remote.use_cases

import androidx.paging.PagingSource
import com.muqp.feature_search.domain.remote.JamendoRepository
import com.muqp.feature_search.model.ArtistUI
import com.muqp.feature_search.model.ItemArtistUI
import javax.inject.Inject

class GetSearchResultToArtistUseCase @Inject constructor(
    private val jamendoRepository: JamendoRepository
) {
    fun pagingInvoke(searchText: String): PagingSource<Int, ItemArtistUI> {
        return jamendoRepository.getSearchPagingResultToArtist(searchText)
    }

    suspend operator fun invoke(searchText: String): ArtistUI {
        return jamendoRepository.getSearchResultToArtist(searchText)
    }
}