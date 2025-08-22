package com.muqp.feature_home.presentation.screen

import androidx.paging.PagingData
import com.muqp.feature_home.model.FeedItemUI

sealed class HomeScreenState {
    data object SignOut : HomeScreenState()
    data class Success(val pagingData: PagingData<FeedItemUI>) : HomeScreenState()
    data class Error(val message: String) : HomeScreenState()
}