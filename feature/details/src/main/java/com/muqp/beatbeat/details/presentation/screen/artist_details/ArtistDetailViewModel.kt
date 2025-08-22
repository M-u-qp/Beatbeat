package com.muqp.beatbeat.details.presentation.screen.artist_details

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muqp.beatbeat.details.domain.local.use_cases.AddTrackToPlaylistUseCase
import com.muqp.beatbeat.details.domain.local.use_cases.DeleteArtistUseCase
import com.muqp.beatbeat.details.domain.local.use_cases.DeleteTrackUseCase
import com.muqp.beatbeat.details.domain.local.use_cases.GetArtistByIdUseCase
import com.muqp.beatbeat.details.domain.local.use_cases.GetPlaylistsUseCase
import com.muqp.beatbeat.details.domain.local.use_cases.GetTrackByIdUseCase
import com.muqp.beatbeat.details.domain.local.use_cases.InsertArtistUseCase
import com.muqp.beatbeat.details.domain.local.use_cases.InsertTrackUseCase
import com.muqp.beatbeat.details.domain.remote.use_cases.GetAllArtistAlbumsUseCase
import com.muqp.beatbeat.details.domain.remote.use_cases.GetPopularArtistTracksUseCase
import com.muqp.beatbeat.details.model.AlbumUI
import com.muqp.beatbeat.details.model.ItemArtistUI
import com.muqp.beatbeat.details.model.ItemTrackUI
import com.muqp.beatbeat.details.model.PlaylistUI
import com.muqp.beatbeat.details.presentation.screen.adapter.ArtistAlbumsDataRecycler
import com.muqp.beatbeat.details.presentation.screen.adapter.PopularArtistTracksDataRecycler
import com.muqp.beatbeat.exo_player.PlayerManager
import com.muqp.core_ui.common.ToggleFavorite
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ArtistDetailViewModel @Inject constructor(
    private val getAllArtistAlbumsUseCase: GetAllArtistAlbumsUseCase,
    private val getArtistByIdUseCase: GetArtistByIdUseCase,
    private val insertArtistUseCase: InsertArtistUseCase,
    private val deleteArtistUseCase: DeleteArtistUseCase,
    private val getPopularArtistTracksUseCase: GetPopularArtistTracksUseCase,
    private val insertTrackUseCase: InsertTrackUseCase,
    private val deleteTrackUseCase: DeleteTrackUseCase,
    private val getTrackByIdUseCase: GetTrackByIdUseCase,
    private val playerManager: PlayerManager,
    private val getPlaylistsUseCase: GetPlaylistsUseCase,
    private val addTrackToPlaylistUseCase: AddTrackToPlaylistUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<ArtistDetailsScreenState?>(null)
    val state = _state.asStateFlow()

    private val _favoriteSideEffect = MutableStateFlow<ToggleFavorite.FavoriteResult?>(null)
    val favoriteSideEffect = _favoriteSideEffect.asStateFlow()

    val coroutineEH = CoroutineExceptionHandler { _, throwable ->
        _state.value = ArtistDetailsScreenState.Error(throwable.message ?: "Unknown error")
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

    suspend fun getFavoriteInitial(item: ItemArtistUI): Boolean {
        return getArtistByIdUseCase.invoke(item.id) != null
    }

    suspend fun toggleArtistFavorite(
        item: ItemArtistUI,
        onResultClicked: (Boolean) -> Unit
    ) {
        val favoriteResult = ToggleFavorite.toggleFavorite(
            item = item,
            isFavorite = { getArtistByIdUseCase.invoke(item.id) != null },
            addToFavorites = { artist ->
                insertArtistUseCase.invoke(artist.copy(isFavorite = true))
            },
            removeFromFavorites = { artist ->
                deleteArtistUseCase.invoke(artist.copy(isFavorite = false))
            },
            onResultClicked = onResultClicked,
            favoriteType = ToggleFavorite.FavoriteType.ARTIST
        )
        if (favoriteResult != null) {
            _favoriteSideEffect.value = favoriteResult
        }
    }

    suspend fun getArtistDetailsData(artistId: Int) {
        _state.value = ArtistDetailsScreenState.Loading
        val loadArtistAlbums = viewModelScope.async(Dispatchers.IO + coroutineEH) {
            getAllArtistAlbumsUseCase.invoke(artistId.toString())
        }

        val loadPopularArtistTracks = viewModelScope.async(Dispatchers.IO + coroutineEH) {
            getPopularArtistTracksUseCase.invoke(artistId)
        }

        try {
            val artistAlbums = loadArtistAlbums.await()
            val popularArtistTracks = loadPopularArtistTracks.await()

            _state.value = ArtistDetailsScreenState.Success(
                albums = artistAlbums,
                popularTracks = popularArtistTracks
            )
        } catch (e: Exception) {
            _state.value = ArtistDetailsScreenState.Error(e.message ?: "")
        }
    }

    fun loadArtistAlbumsDataRecycler(
        albumUI: AlbumUI,
        onClick: (Int) -> Unit
    ): List<ArtistAlbumsDataRecycler> {
        return albumUI.results.map { itemAlbumUI ->
            ArtistAlbumsDataRecycler(
                item = itemAlbumUI,
                onClick = { onClick.invoke(itemAlbumUI.id.toInt()) }
            )
        }
    }

    suspend fun loadPopularArtistTracksDataRecycler(
        tracks: List<ItemTrackUI>,
        onClickTrack: (ItemTrackUI) -> Unit,
        onMenuClickListener: ((View, ItemTrackUI) -> Unit)
    ): List<PopularArtistTracksDataRecycler> {
        return tracks.map { item ->
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
}