package com.muqp.feature_search.presentation.screen.found_result_tab.found_screens.found_albums

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import com.muqp.core_ui.adapters_for_recycler.RecyclerBindable
import com.muqp.core_ui.common.ToggleFavorite
import com.muqp.core_ui.paging.GenericPager
import com.muqp.feature_search.domain.local.use_cases.DeleteAlbumUseCase
import com.muqp.feature_search.domain.local.use_cases.GetAlbumByIdUseCase
import com.muqp.feature_search.domain.local.use_cases.InsertAlbumUseCase
import com.muqp.feature_search.domain.remote.use_cases.GetSearchResultToAlbumUseCase
import com.muqp.feature_search.model.ItemAlbumUI
import com.muqp.feature_search.presentation.adapter.AlbumDataRecycler
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

class FoundAlbumsViewModel @Inject constructor(
    private val getSearchResultToAlbumUseCase: GetSearchResultToAlbumUseCase,
    private val insertAlbumUseCase: InsertAlbumUseCase,
    private val deleteAlbumUseCase: DeleteAlbumUseCase,
    private val getAlbumByIdUseCase: GetAlbumByIdUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<PagingData<ItemAlbumUI>?>(null)
    val state = _state.asStateFlow()

    private val _pagingLoadState = MutableStateFlow(false)
    val pagingLoadState = _pagingLoadState.asStateFlow()

    val coroutineEH = CoroutineExceptionHandler { _, throwable ->
        _errorMessage.value = throwable.message ?: "Unknown error"
    }

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    fun resetErrorMessage() {
        _errorMessage.value = null
    }

    private val _favoriteSideEffect = MutableStateFlow<ToggleFavorite.FavoriteResult?>(null)
    val favoriteSideEffect = _favoriteSideEffect.asStateFlow()

    fun resetFavoriteSideEffect() {
        _favoriteSideEffect.value = null
    }

    fun loadSearchResultToAlbum(searchText: String) {
        GenericPager.paginate(
            pagingSourceFactory = {
                getSearchResultToAlbumUseCase.pagingInvoke(searchText)
            },
            scope = viewModelScope + coroutineEH
        )
            .onStart { _pagingLoadState.value = true }
            .onEach { pagingData ->
                _state.value = pagingData
                _pagingLoadState.value = false
            }
            .launchIn(viewModelScope + coroutineEH)
    }

    fun loadAlbumDataRecycler(
        pagingData: PagingData<ItemAlbumUI>,
        onAlbumClick: (Int) -> Unit,
        onMenuClickListener: ((View, ItemAlbumUI) -> Unit)
    ): PagingData<RecyclerBindable> {
        return pagingData.map { item ->
            AlbumDataRecycler(
                item = item,
                isFavoriteInitial = getAlbumByIdUseCase.invoke(item.id) != null,
                onFavoriteClick = { onResultClicked ->
                    viewModelScope.launch(coroutineEH) {
                        val favoriteResult = ToggleFavorite.toggleFavorite(
                            item = item,
                            isFavorite = { album -> getAlbumByIdUseCase.invoke(album.id) != null },
                            addToFavorites = { album -> insertAlbumUseCase.invoke(album.copy(isFavorite = true)) },
                            removeFromFavorites = { album -> deleteAlbumUseCase.invoke(album.copy(isFavorite = false)) },
                            onResultClicked = onResultClicked,
                            favoriteType = ToggleFavorite.FavoriteType.ALBUM
                        )
                        if (favoriteResult != null) {
                            _favoriteSideEffect.value = favoriteResult
                        }
                    }
                },
                onAlbumClick = { onAlbumClick(item.id.toInt()) },
                onMenuClickListener = onMenuClickListener
            )
        }
    }

    fun updatePagingLoadState(isLoading: Boolean) {
        _pagingLoadState.value = isLoading
    }
}