package com.muqp.feature_home.presentation.screen

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.map
import com.muqp.core_ui.adapters_for_recycler.RecyclerBindable
import com.muqp.core_ui.paging.GenericPager
import com.muqp.feature_home.domain.use_cases.GetMusicFeedsUseCase
import com.muqp.feature_home.domain.use_cases.SignOutUserUseCase
import com.muqp.feature_home.model.FeedItemUI
import com.muqp.feature_home.presentation.screen.adapter.FeedsDataRecycler
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val getMusicFeedsUseCase: GetMusicFeedsUseCase,
    private val signOutUserUseCase: SignOutUserUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<HomeScreenState?>(null)
    val state = _state.asStateFlow()

    val coroutineEH = CoroutineExceptionHandler { _, throwable ->
        _state.value = HomeScreenState.Error(throwable.message ?: "Unknown error")
    }

    private val _pagingLoadState = MutableStateFlow(false)
    val pagingLoadState = _pagingLoadState.asStateFlow()

    fun loadMusicFeeds() {
        GenericPager.paginate(
            pagingSourceFactory = {
                getMusicFeedsUseCase.invoke()
            },
            scope = viewModelScope
        )
            .onStart { _pagingLoadState.value = true }
            .onEach { pagingData ->
                _state.value = HomeScreenState.Success(pagingData)
                _pagingLoadState.value = false
            }
            .catch { e ->
                _state.value = HomeScreenState.Error(
                    e.message ?: ""
                )
                _pagingLoadState.value = false
            }
            .launchIn(viewModelScope)
    }

    fun loadFeedsDataRecycler(
        pagingData: PagingData<FeedItemUI>,
        onMenuClickListener: ((View, FeedItemUI) -> Unit)
    ): PagingData<RecyclerBindable> {
        return pagingData.map { item ->
            FeedsDataRecycler(
                item = item,
                onMenuClickListener = onMenuClickListener
            )
        }
    }

    suspend fun signOut() {
        signOutUserUseCase.invoke()

        _state.value = HomeScreenState.SignOut
    }

    fun handlePaginationLoadState(loadState: CombinedLoadStates) {
        when (val refresh = loadState.refresh) {
            is LoadState.Loading -> {
                _pagingLoadState.value = true
            }

            is LoadState.Error -> {
                _state.value = HomeScreenState.Error(refresh.error.message ?: "")
            }

            else -> {
                _pagingLoadState.value = false
            }
        }
        when (val refresh = loadState.append) {
            is LoadState.Loading -> {
                _pagingLoadState.value = true
            }

            is LoadState.Error -> {
                _state.value = HomeScreenState.Error(refresh.error.message ?: "")
            }

            else -> {
                _pagingLoadState.value = false
            }
        }
    }
}