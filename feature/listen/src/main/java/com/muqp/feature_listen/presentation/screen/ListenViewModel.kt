package com.muqp.feature_listen.presentation.screen

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muqp.beatbeat.exo_player.PlayerManager
import com.muqp.beatbeat.exo_player.model.Track
import com.muqp.core_ui.common.ToggleFavorite
import com.muqp.feature_listen.domain.local.use_cases.AddTrackToPlaylistUseCase
import com.muqp.feature_listen.domain.local.use_cases.DeleteTrackUseCase
import com.muqp.feature_listen.domain.local.use_cases.GetPlaylistsUseCase
import com.muqp.feature_listen.domain.local.use_cases.GetTrackByIdUseCase
import com.muqp.feature_listen.domain.local.use_cases.InsertTrackUseCase
import com.muqp.feature_listen.domain.remote.use_cases.GetAcousticCollectionUseCase
import com.muqp.feature_listen.domain.remote.use_cases.GetElectricCollectionUseCase
import com.muqp.feature_listen.domain.remote.use_cases.GetFastCollectionUseCase
import com.muqp.feature_listen.domain.remote.use_cases.GetFemaleCollectionUseCase
import com.muqp.feature_listen.domain.remote.use_cases.GetMaleCollectionUseCase
import com.muqp.feature_listen.domain.remote.use_cases.GetSlowCollectionUseCase
import com.muqp.feature_listen.model.ItemTrackUI
import com.muqp.feature_listen.model.PlaylistUI
import com.muqp.feature_listen.model.TrackUI
import com.muqp.feature_listen.presentation.adapter.TrackDataRecycler
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ListenViewModel @Inject constructor(
    private val getAcousticCollectionUseCase: GetAcousticCollectionUseCase,
    private val getElectricCollectionUseCase: GetElectricCollectionUseCase,
    private val getFemaleCollectionUseCase: GetFemaleCollectionUseCase,
    private val getMaleCollectionUseCase: GetMaleCollectionUseCase,
    private val getSlowCollectionUseCase: GetSlowCollectionUseCase,
    private val getFastCollectionUseCase: GetFastCollectionUseCase,
    private val insertTrackUseCase: InsertTrackUseCase,
    private val deleteTrackUseCase: DeleteTrackUseCase,
    private val getTrackByIdUseCase: GetTrackByIdUseCase,
    private val playerManager: PlayerManager,
    private val getPlaylistsUseCase: GetPlaylistsUseCase,
    private val addTrackToPlaylistUseCase: AddTrackToPlaylistUseCase
) : ViewModel() {
    private var cachedState: ListenScreenState.Success? = null
    private var isInitialLoad = true

    private val _state = MutableStateFlow<ListenScreenState?>(null)
    val state = _state.asStateFlow()

    private val _favoriteSideEffect = MutableStateFlow<ToggleFavorite.FavoriteResult?>(null)
    val favoriteSideEffect = _favoriteSideEffect.asStateFlow()

    val coroutineEH = CoroutineExceptionHandler { _, throwable ->
        _state.value = ListenScreenState.Error(throwable.message ?: "Unknown error")
    }

    private val _playlists = MutableStateFlow<List<PlaylistUI>>(emptyList())
    val playlists = _playlists.asStateFlow()

    fun onListenCollectionClicked(tracks: List<ItemTrackUI>) {
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

    fun onTrackClicked(itemTrack: ItemTrackUI) {
        playerManager.playTrack(
            trackId = itemTrack.id,
            audioUrl = itemTrack.audio,
            title = itemTrack.name,
            imageUrl = itemTrack.image
        )
    }

    suspend fun loadTrackCollections() {
        if (cachedState != null) {
            _state.value = cachedState
            return
        }
        if (isInitialLoad) {
            _state.value = ListenScreenState.Loading
            val loadAcousticTrackData = viewModelScope.async(Dispatchers.IO + coroutineEH) {
                getAcousticCollectionUseCase.invoke(ACOUSTIC_TAG)
            }
            val loadElectrTrackData = viewModelScope.async(Dispatchers.IO + coroutineEH) {
                getElectricCollectionUseCase.invoke(ELECTRIC_TAG)
            }
            val loadFemaleTrackData = viewModelScope.async(Dispatchers.IO + coroutineEH) {
                getFemaleCollectionUseCase.invoke(FEMALE_TAG)
            }
            val loadMaleTrackData = viewModelScope.async(Dispatchers.IO + coroutineEH) {
                getMaleCollectionUseCase.invoke(MALE_TAG)
            }
            val loadSlowTrackData = viewModelScope.async(Dispatchers.IO + coroutineEH) {
                getSlowCollectionUseCase.invoke(listOf(SLOW_TAG, VERY_SLOW_TAG))
            }
            val loadFastTrackData = viewModelScope.async(Dispatchers.IO + coroutineEH) {
                getFastCollectionUseCase.invoke(listOf(FAST_TAG, VERY_FAST_TAG))
            }

            val acousticData = loadAcousticTrackData.await()
            val electrData = loadElectrTrackData.await()
            val femaleData = loadFemaleTrackData.await()
            val maleData = loadMaleTrackData.await()
            val slowData = loadSlowTrackData.await()
            val fastData = loadFastTrackData.await()

            if (acousticData.results.isEmpty() &&
                electrData.results.isEmpty() &&
                femaleData.results.isEmpty() &&
                maleData.results.isEmpty() &&
                slowData.results.isEmpty() &&
                fastData.results.isEmpty()
            ) {
                _state.value = ListenScreenState.Error("")
            } else {
                _state.value =
                    ListenScreenState.Success(
                        acousticTrackData = acousticData,
                        electrTrackData = electrData,
                        femaleTrackData = femaleData,
                        maleTrackData = maleData,
                        slowTrackData = slowData,
                        fastTrackData = fastData
                    )
            }
            isInitialLoad = false
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

    companion object {
        private const val ACOUSTIC_TAG = "acoustic"
        private const val ELECTRIC_TAG = "electric"
        private const val MALE_TAG = "male"
        private const val FEMALE_TAG = "female"
        private const val VERY_SLOW_TAG = "verylow"
        private const val SLOW_TAG = "low"
        private const val FAST_TAG = "high"
        private const val VERY_FAST_TAG = "veryhigh"
    }
}