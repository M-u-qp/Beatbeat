package com.muqp.feature_favorites.presentation.screen.favorites

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.muqp.beatbeat.exo_player.PlayerManager
import com.muqp.beatbeat.exo_player.model.Track
import com.muqp.feature_favorites.domain.use_cases.CreatePlaylistUseCase
import com.muqp.feature_favorites.domain.use_cases.DeletePlaylistUseCase
import com.muqp.feature_favorites.domain.use_cases.GetPlaylistWithTracksUseCase
import com.muqp.feature_favorites.domain.use_cases.GetPlaylistsUseCase
import com.muqp.feature_favorites.domain.use_cases.GetTrackCountForPlaylistUseCase
import com.muqp.feature_favorites.domain.use_cases.RemoveTrackFromPlaylistUseCase
import com.muqp.feature_favorites.model.ItemTrack
import com.muqp.feature_favorites.model.PlaylistUI
import com.muqp.feature_favorites.presentation.screen.adapter.FavoriteTracksDataRecycler
import com.muqp.feature_favorites.presentation.screen.adapter.PlaylistsDataRecycler
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class FavoritesViewModel @Inject constructor(
    private val createPlaylistUseCase: CreatePlaylistUseCase,
    private val getPlaylistsUseCase: GetPlaylistsUseCase,
    private val getPlaylistWithTracksUseCase: GetPlaylistWithTracksUseCase,
    private val deletePlaylistUseCase: DeletePlaylistUseCase,
    private val removeTrackFromPlaylistUseCase: RemoveTrackFromPlaylistUseCase,
    private val getTrackCountForPlaylistUseCase: GetTrackCountForPlaylistUseCase,
    private val playerManager: PlayerManager
) : ViewModel() {
    private val _playlists = MutableStateFlow<List<PlaylistUI>>(emptyList())
    val playlists = _playlists.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    val coroutineEH = CoroutineExceptionHandler { _, throwable ->
        _errorMessage.value = throwable.message ?: "Unknown error"
    }

    private val _tracks = MutableStateFlow<List<ItemTrack>>(emptyList())
    val tracks = _tracks.asStateFlow()

    init {
        viewModelScope.launch(coroutineEH) {
            getPlaylists()
        }
    }

    suspend fun getPlaylistTracks(playlistId: Long) {
        _tracks.value = getPlaylistWithTracksUseCase.invoke(playlistId).second
    }

    suspend fun deletePlaylist(playlistId: Long) {
        deletePlaylistUseCase.invoke(playlistId)
        getPlaylists()
    }

    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: String) {
        removeTrackFromPlaylistUseCase.invoke(playlistId, trackId)
        getPlaylistTracks(playlistId)
        getPlaylists()
    }

    private suspend fun getTrackCountForPlaylist(playlistId: Long): Int {
        return getTrackCountForPlaylistUseCase.invoke(playlistId)
    }

    fun loadPlaylistTracksDataRecycler(
        tracks: List<ItemTrack>,
        onClickTrack: (ItemTrack) -> Unit,
        onMenuClickListener: ((View, ItemTrack) -> Unit)
    ): List<FavoriteTracksDataRecycler> {
        return tracks.map { item ->
            FavoriteTracksDataRecycler(
                item = item,
                onClickTrack = { onClickTrack(item) },
                onMenuClickListener = onMenuClickListener
            )
        }
    }

    suspend fun createPlaylist(
        name: String,
        description: String? = null
    ) {
        createPlaylistUseCase.invoke(name, description)
        getPlaylists()
    }

    suspend fun getPlaylists() {
        val allPlaylists = getPlaylistsUseCase.invoke().map { playlist ->
            val trackCount =
                playlist.id?.let { getTrackCountForPlaylist(it) } ?: 0
            playlist.copy(trackCount = trackCount)
        }
        _playlists.value = allPlaylists
    }

    fun loadPlaylistDataRecycler(
        playlists: List<PlaylistUI>,
        onDeletePlaylist: (Long) -> Unit,
        onPlaylistClicked: (Long) -> Unit,
        onBindTracks: (RecyclerView, Long?) -> Unit
    ): List<PlaylistsDataRecycler> {
        return playlists.map { item ->
            PlaylistsDataRecycler(
                item = item,
                onDeletePlaylist = onDeletePlaylist,
                onPlaylistClicked = onPlaylistClicked,
                onBindTracks = onBindTracks
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

    suspend fun onPlaylistClicked(playlistId: Long) {
        val tracks = getPlaylistWithTracksUseCase.invoke(playlistId).second
        if (tracks.isNotEmpty()) {
            val playlistTracks = tracks.map { track ->
                Track(
                    id = track.id,
                    title = track.name,
                    imageUrl = track.image,
                    audioUrl = track.audio
                )
            }
            playerManager.setPlaylist(playlistTracks)
        }
    }

    fun resetErrorMessage() {
        _errorMessage.value = null
    }
}