package com.muqp.feature_search.presentation.screen

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muqp.beatbeat.exo_player.PlayerManager
import com.muqp.core_ui.common.ToggleFavorite
import com.muqp.feature_search.domain.local.use_cases.AddTrackToPlaylistUseCase
import com.muqp.feature_search.domain.local.use_cases.DeleteAlbumUseCase
import com.muqp.feature_search.domain.local.use_cases.DeleteArtistUseCase
import com.muqp.feature_search.domain.local.use_cases.DeleteTrackUseCase
import com.muqp.feature_search.domain.local.use_cases.GetAlbumByIdUseCase
import com.muqp.feature_search.domain.local.use_cases.GetArtistByIdUseCase
import com.muqp.feature_search.domain.local.use_cases.GetPlaylistsUseCase
import com.muqp.feature_search.domain.local.use_cases.GetTrackByIdUseCase
import com.muqp.feature_search.domain.local.use_cases.InsertAlbumUseCase
import com.muqp.feature_search.domain.local.use_cases.InsertArtistUseCase
import com.muqp.feature_search.domain.local.use_cases.InsertTrackUseCase
import com.muqp.feature_search.domain.remote.use_cases.GetSearchResultToAlbumUseCase
import com.muqp.feature_search.domain.remote.use_cases.GetSearchResultToArtistUseCase
import com.muqp.feature_search.domain.remote.use_cases.GetSearchResultToTrackUseCase
import com.muqp.feature_search.model.AlbumUI
import com.muqp.feature_search.model.ArtistUI
import com.muqp.feature_search.model.ItemAlbumUI
import com.muqp.feature_search.model.ItemArtistUI
import com.muqp.feature_search.model.ItemTrackUI
import com.muqp.feature_search.model.PlaylistUI
import com.muqp.feature_search.model.TrackUI
import com.muqp.feature_search.presentation.adapter.AlbumDataRecycler
import com.muqp.feature_search.presentation.adapter.ArtistDataRecycler
import com.muqp.feature_search.presentation.adapter.TrackDataRecycler
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    private val getSearchResultToTrackUseCase: GetSearchResultToTrackUseCase,
    private val getSearchResultToAlbumUseCase: GetSearchResultToAlbumUseCase,
    private val getSearchResultToArtistUseCase: GetSearchResultToArtistUseCase,
    private val insertTrackUseCase: InsertTrackUseCase,
    private val deleteTrackUseCase: DeleteTrackUseCase,
    private val getTrackByIdUseCase: GetTrackByIdUseCase,
    private val insertAlbumUseCase: InsertAlbumUseCase,
    private val deleteAlbumUseCase: DeleteAlbumUseCase,
    private val getAlbumByIdUseCase: GetAlbumByIdUseCase,
    private val insertArtistUseCase: InsertArtistUseCase,
    private val deleteArtistUseCase: DeleteArtistUseCase,
    private val getArtistByIdUseCase: GetArtistByIdUseCase,
    private val playerManager: PlayerManager,
    private val getPlaylistsUseCase: GetPlaylistsUseCase,
    private val addTrackToPlaylistUseCase: AddTrackToPlaylistUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<SearchScreenState?>(null)
    val state = _state.asStateFlow()

    private val _favoriteSideEffect = MutableStateFlow<ToggleFavorite.FavoriteResult?>(null)
    val favoriteSideEffect = _favoriteSideEffect.asStateFlow()

    private val _playlists = MutableStateFlow<List<PlaylistUI>>(emptyList())
    val playlists = _playlists.asStateFlow()

    val coroutineEH = CoroutineExceptionHandler { _, throwable ->
        _state.value = SearchScreenState.Error(throwable.message ?: "Unknown error")
    }

    var currentSearchText = ""

    init {
        _state.value = SearchScreenState.NoInputData
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

    suspend fun loadSearchResult(searchText: String) {
        currentSearchText = searchText
        try {
            _state.value = SearchScreenState.Loading
            val loadTrackData = viewModelScope.async(Dispatchers.IO + coroutineEH) {
                getSearchResultToTrackUseCase.invoke(searchText)
            }
            val loadAlbumData = viewModelScope.async(Dispatchers.IO + coroutineEH) {
                getSearchResultToAlbumUseCase.invoke(searchText)
            }
            val loadArtistData = viewModelScope.async(Dispatchers.IO + coroutineEH) {
                getSearchResultToArtistUseCase.invoke(searchText)
            }

            val trackData = loadTrackData.await()
            val albumData = loadAlbumData.await()
            val artistData = loadArtistData.await()

            if (trackData.results.isEmpty() && albumData.results.isEmpty() && artistData.results.isEmpty()) {
                _state.value = SearchScreenState.NotFound
            } else {
                _state.value =
                    SearchScreenState.Success(
                        trackData = trackData,
                        albumData = albumData,
                        artistData = artistData
                    )
            }

        } catch (_: CancellationException) {
            _state.value = SearchScreenState.NoInputData
        } catch (e: Exception) {
            _state.value = SearchScreenState.Error(e.message ?: "")
        }
    }

    suspend fun loadTrackDataRecycler(
        track: TrackUI?,
        onClickTrack: (ItemTrackUI) -> Unit,
        onMenuClickListener: ((View, ItemTrackUI) -> Unit)
    ): List<TrackDataRecycler>? {
        return track?.let {
            it.results.map { item ->
                TrackDataRecycler(
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
    }

    fun onTrackClicked(itemTrack: ItemTrackUI) {
        playerManager.playTrack(
            trackId = itemTrack.id,
            audioUrl = itemTrack.audio,
            title = itemTrack.name,
            imageUrl = itemTrack.image
        )
    }

    suspend fun loadAlbumDataRecycler(
        data: AlbumUI?,
        onAlbumClick: (Int) -> Unit,
        onMenuClickListener: ((View, ItemAlbumUI) -> Unit)
    ): List<AlbumDataRecycler>? {
        return data?.let {
            it.results.map { item ->
                AlbumDataRecycler(
                    item = item,
                    isFavoriteInitial = getAlbumByIdUseCase.invoke(item.id) != null,
                    onFavoriteClick = { onResultClicked ->
                        viewModelScope.launch(coroutineEH) {
                            val favoriteResult = ToggleFavorite.toggleFavorite(
                                item = item,
                                isFavorite = { album -> getAlbumByIdUseCase.invoke(album.id) != null },
                                addToFavorites = { album ->
                                    insertAlbumUseCase.invoke(
                                        album.copy(
                                            isFavorite = true
                                        )
                                    )
                                },
                                removeFromFavorites = { album ->
                                    deleteAlbumUseCase.invoke(
                                        album.copy(
                                            isFavorite = false
                                        )
                                    )
                                },
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
    }

    suspend fun loadArtistDataRecycler(
        data: ArtistUI?,
        onArtistClick: (ItemArtistUI) -> Unit,
        onMenuClickListener: ((View, ItemArtistUI) -> Unit)
    ): List<ArtistDataRecycler>? {
        return data?.let {
            it.results.map { item ->
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
    }
}