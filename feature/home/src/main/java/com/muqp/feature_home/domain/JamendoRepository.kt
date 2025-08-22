package com.muqp.feature_home.domain

import androidx.paging.PagingSource
import com.muqp.feature_home.model.FeedItemUI

interface JamendoRepository {
    fun getMusicFeeds(): PagingSource<Int, FeedItemUI>
}