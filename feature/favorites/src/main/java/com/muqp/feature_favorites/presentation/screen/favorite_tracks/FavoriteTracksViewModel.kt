package com.muqp.feature_favorites.presentation.screen.favorite_tracks

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muqp.beatbeat.exo_player.PlayerManager
import com.muqp.feature_favorites.domain.use_cases.AddTrackToPlaylistUseCase
import com.muqp.feature_favorites.domain.use_cases.DeleteTrackUseCase
import com.muqp.feature_favorites.domain.use_cases.GetAllTracksUseCase
import com.muqp.feature_favorites.domain.use_cases.GetPlaylistsUseCase
import com.muqp.feature_favorites.model.ItemTrack
import com.muqp.feature_favorites.model.PlaylistUI
import com.muqp.feature_favorites.presentation.screen.adapter.FavoriteTracksDataRecycler
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

class FavoriteTracksViewModel @Inject constructor(
    getAllTracksUseCase: GetAllTracksUseCase,
    private val deleteTrackUseCase: DeleteTrackUseCase,
    private val playerManager: PlayerManager,
    private val getPlaylistsUseCase: GetPlaylistsUseCase,
    private val addTrackToPlaylistUseCase: AddTrackToPlaylistUseCase
) : ViewModel() {
    private val _playlists = MutableStateFlow<List<PlaylistUI>>(emptyList())
    val playlists = _playlists.asStateFlow()

    val coroutineEH = CoroutineExceptionHandler { _, throwable ->
        _errorMessage.value = throwable.message ?: "Unknown error"
    }

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    fun resetErrorMessage() {
        _errorMessage.value = null
    }

    val getAllTracks = getAllTracksUseCase()
        .stateIn(
            scope = viewModelScope + coroutineEH,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    suspend fun addTrackToPlaylist(playlistId: Long, trackId: String) {
        addTrackToPlaylistUseCase.invoke(playlistId, trackId)
    }

    fun getPlaylists() {
        viewModelScope.launch(coroutineEH) {
            _playlists.value = getPlaylistsUseCase.invoke()
        }
    }

    fun loadFavoriteTracksDataRecycler(
        listTracks: List<ItemTrack>,
        onClickTrack: (ItemTrack) -> Unit,
        onMenuClickListener: ((View, ItemTrack) -> Unit)
    ): List<FavoriteTracksDataRecycler> {
        return listTracks.map { item ->
            FavoriteTracksDataRecycler(
                item = item,
                onClickTrack = { onClickTrack(item) },
                onMenuClickListener = onMenuClickListener
            )
        }
    }

    fun onTrackClicked(itemTrack: ItemTrack) {
        playerManager.playTrack(
            trackId = itemTrack.id,
            audioUrl = itemTrack.audio,
            title = itemTrack.name,
            imageUrl = itemTrack.image
        )
    }

    suspend fun deleteTrack(track: ItemTrack) {
        deleteTrackUseCase.invoke(track)
    }
}