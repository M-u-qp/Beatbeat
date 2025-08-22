package com.muqp.beatbeat.exo_player

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.muqp.beatbeat.exo_player.background.BackgroundPlay
import com.muqp.beatbeat.exo_player.background.BackgroundService
import com.muqp.beatbeat.exo_player.model.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class PlayerManager @Inject constructor(
    private val backgroundPlay: BackgroundPlay,
    private val context: Application
) {
    private val _currentTrack = MutableStateFlow<Track?>(null)
    val currentTrack = _currentTrack.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _playbackPosition = MutableStateFlow(0L)
    val playbackPosition = _playbackPosition.asStateFlow()

    private val _playbackDuration = MutableStateFlow(0L)
    val playbackDuration = _playbackDuration.asStateFlow()

    private val _playerUiEvents = MutableSharedFlow<PlayerUiEvent>()
    val playerUiEvents = _playerUiEvents.asSharedFlow()

    private val _currentPlaylist = MutableStateFlow<List<Track>>(emptyList())

    private val _currentPlaylistIndex = MutableStateFlow(-1)
    val currentPlaylistIndex = _currentPlaylistIndex.asStateFlow()

    sealed class PlayerUiEvent {
        data object ShowPlayer : PlayerUiEvent()
        data object HidePlayer : PlayerUiEvent()
    }

    private var service: BackgroundService? = null
    private var serviceConnection: ServiceConnection? = null
    private var playerStateJob: Job? = null

    init {
        bindToService()
    }

    private fun bindToService() {
        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
                if (binder is BackgroundService.LocalBinder) {
                    service = binder.getService()
                    observeServiceState()
                }
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                service = null
                playerStateJob?.cancel()
            }
        }
        val intent = Intent(context, BackgroundService::class.java)
        context.bindService(intent, serviceConnection as ServiceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun observeServiceState() {
        playerStateJob?.cancel()
        playerStateJob = CoroutineScope(Dispatchers.Main).launch {
            service?.playerState?.collect { state ->
                _playbackPosition.value = state.position
                _playbackDuration.value = state.duration
                _isPlaying.value = state.isPlaying
                state.currentTrack?.let {
                    _currentTrack.value = it
                }
                if (state.playlist.isNotEmpty()) {
                    _currentPlaylist.value = state.playlist
                    _currentPlaylistIndex.value = state.currentPlaylistIndex
                }
                if (state.isVisibilityPlayer) {
                    showPlayer()
                } else {
                    hidePlayer()
                }
            }
        }
    }

    private fun showPlayer() {
        CoroutineScope(Dispatchers.Main).launch {
            _playerUiEvents.emit(PlayerUiEvent.ShowPlayer)
        }
    }

    private fun hidePlayer() {
        CoroutineScope(Dispatchers.Main).launch {
            _playerUiEvents.emit(PlayerUiEvent.HidePlayer)
        }
    }

    fun playTrack(
        trackId: String,
        audioUrl: String,
        title: String,
        imageUrl: String
    ) {
        _playbackPosition.value = 0
        _playbackDuration.value = 0
        if (currentTrack.value?.id != trackId) {
            _currentTrack.value = Track(
                id = trackId,
                title = title,
                imageUrl = imageUrl,
                audioUrl = audioUrl
            )
            _isPlaying.value = true
            clearPlaylist()
            backgroundPlay.start(
                url = audioUrl,
                startPosition = playbackPosition.value,
                title = title,
                imageUrl = imageUrl,
                trackId = trackId
            )
            showPlayer()
        } else {
            togglePlayPause()
        }
    }

    private fun pauseTrack() {
        _isPlaying.value = false
        backgroundPlay.pause()
    }

    private fun resumeTrack() {
        _isPlaying.value = true
        backgroundPlay.resume(playbackPosition.value)
    }

    fun stopTrack() {
        _isPlaying.value = false
        _currentTrack.value = null
        _playbackDuration.value = 0L
        _playbackPosition.value = 0L
        backgroundPlay.stop()
        hidePlayer()
    }

    fun togglePlayPause() {
        if (isPlaying.value) {
            pauseTrack()
        } else {
            resumeTrack()
        }
    }

    fun seekTo(positionMs: Long) {
        backgroundPlay.seekTo(positionMs)
    }

    fun setPlaylist(tracks: List<Track>, startIndex: Int = 0) {
        _currentPlaylist.value = tracks
        _currentPlaylistIndex.value = startIndex

        backgroundPlay.startPlaylist(
            tracks = tracks,
            startIndex = startIndex
        )

        _currentTrack.value = tracks[startIndex]
        _isPlaying.value = true
        showPlayer()
    }

    fun seekToNext(): Boolean {
        return backgroundPlay.next()
    }

    fun seekToPrevious(): Boolean {
        return backgroundPlay.previous()
    }

    suspend fun checkServiceState() {
        val isStopped = service?.isPlayerStopped() ?: true
        if (isStopped) {
            _playerUiEvents.emit(PlayerUiEvent.HidePlayer)
        }
    }

    private fun clearPlaylist() {
        _currentPlaylist.value = emptyList()
        _currentPlaylistIndex.value = -1
    }
}