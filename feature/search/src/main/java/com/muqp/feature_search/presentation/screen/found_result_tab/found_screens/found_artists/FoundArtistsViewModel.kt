package com.muqp.feature_search.presentation.screen.found_result_tab.found_screens.found_artists

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import com.muqp.core_ui.adapters_for_recycler.RecyclerBindable
import com.muqp.core_ui.common.ToggleFavorite
import com.muqp.core_ui.paging.GenericPager
import com.muqp.feature_search.domain.local.use_cases.DeleteArtistUseCase
import com.muqp.feature_search.domain.local.use_cases.GetArtistByIdUseCase
import com.muqp.feature_search.domain.local.use_cases.InsertArtistUseCase
import com.muqp.feature_search.domain.remote.use_cases.GetSearchResultToArtistUseCase
import com.muqp.feature_search.model.ItemArtistUI
import com.muqp.feature_search.presentation.adapter.ArtistDataRecycler
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

class FoundArtistsViewModel @Inject constructor(
    private val getSearchResultToArtistUseCase: GetSearchResultToArtistUseCase,
    private val insertArtistUseCase: InsertArtistUseCase,
    private val deleteArtistUseCase: DeleteArtistUseCase,
    private val getArtistByIdUseCase: GetArtistByIdUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<PagingData<ItemArtistUI>?>(null)
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

    fun loadSearchResultToTrack(searchText: String) {
        GenericPager.paginate(
            pagingSourceFactory = {
                getSearchResultToArtistUseCase.pagingInvoke(searchText)
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

    fun loadArtistDataRecycler(
        pagingData: PagingData<ItemArtistUI>,
        onArtistClick: (ItemArtistUI) -> Unit,
        onMenuClickListener: ((View, ItemArtistUI) -> Unit)
    ): PagingData<RecyclerBindable> {
        return pagingData.map { item ->
            ArtistDataRecycler(
                item = item,
                isFavoriteInitial = getArtistByIdUseCase.invoke(item.id) != null,
                onFavoriteClick = { onResultClicked ->
                    viewModelScope.launch(coroutineEH) {
                        val favoriteResult = ToggleFavorite.toggleFavorite(
                            item = item,
                            isFavorite = { artist -> getArtistByIdUseCase.invoke(artist.id) != null },
                            addToFavorites = { artist ->
                                insertArtistUseCase.invoke(
                                    artist.copy(
                                        isFavorite = true
                                    )
                                )
                            },
                            removeFromFavorites = { artist ->
                                deleteArtistUseCase.invoke(
                                    artist.copy(isFavorite = false)
                                )
                            },
                            onResultClicked = onResultClicked,
                            favoriteType = ToggleFavorite.FavoriteType.ARTIST
                        )
                        if (favoriteResult != null) {
                            _favoriteSideEffect.value = favoriteResult
                        }
                    }
                },
                onArtistClick = { onArtistClick(item) },
                onMenuClickListener = onMenuClickListener
            )
        }
    }

    fun updatePagingLoadState(isLoading: Boolean) {
        _pagingLoadState.value = isLoading
    }
}