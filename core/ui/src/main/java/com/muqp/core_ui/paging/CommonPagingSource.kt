package com.muqp.core_ui.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState

class CommonPagingSource<T : Any>(
    private val loadData: suspend (page: Int) -> PagingData<T>,
    private val getNextKey: (currentPage: Int, totalItems: Int) -> Int?,
    private val getPrevKey: (currentPage: Int) -> Int? = { null }
) : PagingSource<Int, T>() {
    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val page = params.key ?: 1
        return try {
            val pagingData = loadData(page)
            LoadResult.Page(
                data = pagingData.items,
                nextKey = getNextKey(page, pagingData.items.size),
                prevKey = getPrevKey(page)
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}

data class PagingData<T>(
    val items: List<T>,
    val total: Int
)