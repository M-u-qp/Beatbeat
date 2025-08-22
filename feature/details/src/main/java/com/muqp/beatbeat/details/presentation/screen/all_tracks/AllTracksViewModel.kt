package com.muqp.beatbeat.details.presentation.screen.all_tracks

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import com.muqp.beatbeat.details.domain.local.use_cases.AddTrackToPlaylistUseCase
import com.muqp.beatbeat.details.domain.local.use_cases.DeleteTrackUseCase
import com.muqp.beatbeat.details.domain.local.use_cases.GetPlaylistsUseCase
import com.muqp.beatbeat.details.domain.local.use_cases.GetTrackByIdUseCase
import com.muqp.beatbeat.details.domain.local.use_cases.InsertTrackUseCase
import com.muqp.beatbeat.details.domain.remote.use_cases.GetPagingPopularArtistTracksUseCase
import com.muqp.beatbeat.details.model.ItemTrackUI
import com.muqp.beatbeat.details.model.PlaylistUI
import com.muqp.beatbeat.details.presentation.screen.adapter.PopularArtistTracksDataRecycler
import com.muqp.beatbeat.exo_player.PlayerManager
import com.muqp.core_ui.adapters_for_recycler.RecyclerBindable
import com.muqp.core_ui.common.ToggleFavorite
import com.muqp.core_ui.paging.GenericPager
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

class AllTracksViewModel @Inject constructor(
    private val getPagingPopularArtistTracksUseCase: GetPagingPopularArtistTracksUseCase,
    private val insertTrackUseCase: InsertTrackUseCase,
    private val deleteTrackUseCase: DeleteTrackUseCase,
    private val getTrackByIdUseCase: GetTrackByIdUseCase,
    private val playerManager: PlayerManager,
    private val getPlaylistsUseCase: GetPlaylistsUseCase,
    private val addTrackToPlaylistUseCase: AddTrackToPlaylistUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<PagingData<ItemTrackUI>?>(null)
    val state = _state.asStateFlow()

    private val _pagingLoadState = MutableStateFlow(false)
    val pagingLoadState = _pagingLoadState.asStateFlow()

    private val _favoriteSideEffect = MutableStateFlow<ToggleFavorite.FavoriteResult?>(null)
    val favoriteSideEffect = _favoriteSideEffect.asStateFlow()

    val coroutineEH = CoroutineExceptionHandler { _, throwable ->
        _errorMessage.value = throwable.message ?: "Unknown error"
    }

    private val _playlists = MutableStateFlow<List<PlaylistUI>>(emptyList())
    val playlists = _playlists.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    fun resetErrorMessage() {
        _errorMessage.value = null
    }

    suspend fun addTrackToPlaylist(playlistId: Long, track: ItemTrackUI) {
        insertTrackUseCase.invoke(track)
        addTrackToPlaylistUseCase.invoke(playlistId, track.id)
    }

    suspend fun getPlaylists() {
            _playlists.value = getPlaylistsUseCase.invoke()
    }

    fun resetFavoriteSideEffect() {
        _favoriteSideEffect.value = null
    }

    fun loadAllPopularTracksByArtist(artistId: Int) {
        GenericPager.paginate(
            pagingSourceFactory = {
                getPagingPopularArtistTracksUseCase.invoke(artistId)
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

    fun loadPopularArtistTracksDataRecycler(
        pagingData: PagingData<ItemTrackUI>,
        onClickTrack: (ItemTrackUI) -> Unit,
        onMenuClickListener: ((View, ItemTrackUI) -> Unit)
    ): PagingData<RecyclerBindable> {
        return pagingData.map { item ->
            PopularArtistTracksDataRecycler(
                item = item,
                isFavoriteInitial = getTrackByIdUseCase.invoke(item.id) != null,
                onFavoriteClick = { onResultClicked ->
                    viewModelScope.launch(coroutineEH) {
                        val favoriteResult = ToggleFavorite.toggleFavorite(
                            item = item,
                            isFavorite = { track -> getTrackByIdUseCase.invoke(track.id) != null },
                            addToFavorites = { track ->
                                insertTrackUseCase.invoke(
                                    track.copy(
                                        isFavorite = true
                                    )
                                )
                            },
                            removeFromFavorites = { track ->
                                deleteTrackUseCase.invoke(
                                    track.copy(
                                        isFavorite = false
                                    )
                                )
                            },
                            onResultClicked = onResultClicked,
                            favoriteType = ToggleFavorite.FavoriteType.TRACK
                        )
                        if (favoriteResult != null) {
                            _favoriteSideEffect.value = favoriteResult
                        }
                    }
                },
                onMenuClickListener = onMenuClickListener,
                onClickTrack = { onClickTrack(item) }
            )
        }
    }

    fun onTrackClicked(itemTrack: ItemTrackUI) {
        playerManager.playTrack(
            trackId = itemTrack.id,
            audioUrl = itemTrack.audio,
            title = itemTrack.name,
            imageUrl = ""
        )
    }

    fun updatePagingLoadState(isLoading: Boolean) {
        _pagingLoadState.value = isLoading
    }
}