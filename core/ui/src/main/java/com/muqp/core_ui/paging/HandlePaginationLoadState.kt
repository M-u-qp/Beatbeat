package com.muqp.core_ui.paging

import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState

object HandlePaginationLoadState {
    fun handlePaginationLoadState(
        loadState: CombinedLoadStates,
        onLoadingStateChanged: (Boolean) -> Unit
    ) {
        when (loadState.refresh) {
            is LoadState.Loading -> {
                onLoadingStateChanged(true)
            }

            else -> {
                onLoadingStateChanged(false)
            }
        }
        when (loadState.append) {
            is LoadState.Loading -> {
                onLoadingStateChanged(true)
            }

            else -> {
                onLoadingStateChanged(false)
            }
        }
    }
}