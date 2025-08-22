package com.muqp.beatbeat.details.presentation.screen.album_details

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muqp.beatbeat.details.domain.local.use_cases.AddTrackToPlaylistUseCase
import com.muqp.beatbeat.details.domain.local.use_cases.DeleteAlbumUseCase
import com.muqp.beatbeat.details.domain.local.use_cases.DeleteTrackUseCase
import com.muqp.beatbeat.details.domain.local.use_cases.GetAlbumByIdUseCase
import com.muqp.beatbeat.details.domain.local.use_cases.GetPlaylistsUseCase
import com.muqp.beatbeat.details.domain.local.use_cases.GetTrackByIdUseCase
import com.muqp.beatbeat.details.domain.local.use_cases.InsertAlbumUseCase
import com.muqp.beatbeat.details.domain.local.use_cases.InsertTrackUseCase
import com.muqp.beatbeat.details.domain.remote.use_cases.GetAlbumDetailsUseCase
import com.muqp.beatbeat.details.model.ItemAlbumUI
import com.muqp.beatbeat.details.model.ItemTrackUI
import com.muqp.beatbeat.details.model.PlaylistUI
import com.muqp.beatbeat.details.presentation.screen.adapter.AlbumTracksDataRecycler
import com.muqp.beatbeat.exo_player.PlayerManager
import com.muqp.core_ui.common.ToggleFavorite
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class AlbumDetailViewModel @Inject constructor(
    private val getAlbumDetailsUseCase: GetAlbumDetailsUseCase,
    private val insertTrackUseCase: InsertTrackUseCase,
    private val deleteTrackUseCase: DeleteTrackUseCase,
    private val getTrackByIdUseCase: GetTrackByIdUseCase,
    private val insertAlbumUseCase: InsertAlbumUseCase,
    private val deleteAlbumUseCase: DeleteAlbumUseCase,
    private val getAlbumByIdUseCase: GetAlbumByIdUseCase,
    private val playerManager: PlayerManager,
    private val getPlaylistsUseCase: GetPlaylistsUseCase,
    private val addTrackToPlaylistUseCase: AddTrackToPlaylistUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<AlbumDetailsScreenState?>(null)
    val state = _state.asStateFlow()

    private val _favoriteSideEffect = MutableStateFlow<ToggleFavorite.FavoriteResult?>(null)
    val favoriteSideEffect = _favoriteSideEffect.asStateFlow()

    val coroutineEH = CoroutineExceptionHandler { _, throwable ->
        _state.value = AlbumDetailsScreenState.Error(throwable.message ?: "Unknown error")
    }

    private val _playlists = MutableStateFlow<List<PlaylistUI>>(emptyList())
    val playlists = _playlists.asStateFlow()

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

    suspend fun getFavoriteInitial(item: ItemAlbumUI): Boolean {
        return getAlbumByIdUseCase.invoke(item.id) != null
    }

    suspend fun toggleAlbumFavorite(
        item: ItemAlbumUI,
        onResultClicked: (Boolean) -> Unit
    ) {
        val favoriteResult = ToggleFavorite.toggleFavorite(
            item = item,
            isFavorite = { getAlbumByIdUseCase.invoke(item.id) != null },
            addToFavorites = { album ->
                insertAlbumUseCase.invoke(album.copy(isFavorite = true))
            },
            removeFromFavorites = { album ->
                deleteAlbumUseCase.invoke(album.copy(isFavorite = false))
            },
            onResultClicked = onResultClicked,
            favoriteType = ToggleFavorite.FavoriteType.ALBUM
        )
        if (favoriteResult != null) {
            _favoriteSideEffect.value = favoriteResult
        }
    }

    suspend fun loadAlbumById(albumId: Int) {
        _state.value = AlbumDetailsScreenState.Loading
        val getAlbumDetails = getAlbumDetailsUseCase.invoke(albumId)
        _state.value =
            AlbumDetailsScreenState.Success(
                albumData = getAlbumDetails.results.first()
            )
    }

    suspend fun loadAlbumTracksDataRecycler(
        item: ItemAlbumUI,
        onClickTrack: (ItemTrackUI) -> Unit,
        onMenuClickListener: ((View, ItemTrackUI) -> Unit)
    ): List<AlbumTracksDataRecycler> {
        return item.tracks.map { track ->
            AlbumTracksDataRecycler(
                item = track,
                isFavoriteInitial = getTrackByIdUseCase.invoke(track.id) != null,
                onFavoriteClick = { onResultClicked ->
                    viewModelScope.launch(coroutineEH) {
                        val favoriteResult =
                            ToggleFavorite.toggleFavorite(
                                item = track,
                                isFavorite = { track -> getTrackByIdUseCase.invoke(track.id) != null },
                                addToFavorites = { track ->
                                    insertTrackUseCase.invoke(
                                        track.copy(isFavorite = true)
                                    )
                                },
                                removeFromFavorites = { track ->
                                    deleteTrackUseCase.invoke(
                                        track.copy(isFavorite = false)
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
                onClickTrack = { onClickTrack(track) }
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
}