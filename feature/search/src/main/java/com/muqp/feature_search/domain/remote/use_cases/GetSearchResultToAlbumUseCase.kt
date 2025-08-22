package com.muqp.feature_search.domain.remote.use_cases

import androidx.paging.PagingSource
import com.muqp.feature_search.domain.remote.JamendoRepository
import com.muqp.feature_search.model.AlbumUI
import com.muqp.feature_search.model.ItemAlbumUI
import javax.inject.Inject

class GetSearchResultToAlbumUseCase @Inject constructor(
    private val jamendoRepository: JamendoRepository
) {
     fun pagingInvoke(searchText: String): PagingSource<Int, ItemAlbumUI> {
        return jamendoRepository.getSearchPagingResultToAlbum(searchText)
    }

    suspend operator fun invoke(searchText: String): AlbumUI {
        return jamendoRepository.getSearchResultToAlbum(searchText)
    }
}