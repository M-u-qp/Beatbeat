package com.muqp.core_ui.paging

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

object GenericPager {
    fun <T : Any> paginate(
        pagingSourceFactory: () -> PagingSource<Int, T>,
        scope: CoroutineScope,
        pageSize: Int = 10
    ): Flow<PagingData<T>> {
        return Pager(
            config = PagingConfig(pageSize = pageSize),
            pagingSourceFactory = pagingSourceFactory
        ).flow
            .cachedIn(scope)
    }
}