package com.muqp.beatbeat.exo_player.background

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.muqp.beatbeat.exo_player.model.Track
import com.muqp.beatbeat.exo_player.notification.NotificationHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class BackgroundService : LifecycleService() {
    private val _playerState = MutableStateFlow(PlayerState())
    val playerState = _playerState.asStateFlow()

    data class PlayerState(
        val position: Long = 0L,
        val duration: Long = 0L,
        val isPlaying: Boolean = false,
        val currentTrack: Track? = null,
        val playlist: List<Track> = emptyList(),
        val currentPlaylistIndex: Int = -1,
        val isVisibilityPlayer: Boolean = false
    )

    private var exoPlayer: ExoPlayer? = null
    private lateinit var notificationHelper: NotificationHelper
    private var playerStateJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        notificationHelper = NotificationHelper(this.application)
        initializePlayer()
        handleStart()
    }

    private fun initializePlayer() {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(this.application)
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(C.USAGE_MEDIA)
                        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                        .build(),
                    true
                )
                .setHandleAudioBecomingNoisy(true)
                .build()
                .apply {
                    repeatMode = Player.REPEAT_MODE_OFF
                }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        intent?.action?.let { action ->
            when (action) {
                ACTION_PAUSE -> handlePause()
                ACTION_PLAY -> handlePlay(intent)
                ACTION_START -> handleStart()
                ACTION_RESUME -> handleResume()
                ACTION_STOP -> handleStop()
                ACTION_NEXT -> handleNext()
                ACTION_PREVIOUS -> handlePrevious()
                ACTION_SEEK_TO -> handleSeekTo(intent)
            }
        }

        return START_STICKY
    }

    private fun startForegroundWithNotification() {
        val notification = notificationHelper.getNotification(playerState.value.isPlaying)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NotificationHelper.NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            startForeground(NotificationHelper.NOTIFICATION_ID, notification)
        }
    }

    private fun handleStart() {
        if (playerStateJob == null) {
            playerStateJob = observePlayerState()
        }
    }

    private fun handleSeekTo(intent: Intent) {
        val positionMs = intent.getLongExtra(SEEK_POSITION, 0L)
        exoPlayer?.seekTo(positionMs)
        updatePlayerState()
    }

    private fun updatePlayerState() {
        exoPlayer?.let { player ->
            val position = player.currentPosition.coerceAtLeast(0)
            val duration = player.duration.takeIf { it > 0 } ?: 0L
            _playerState.update { current ->
                if (
                    current.position != position ||
                    current.duration != duration ||
                    current.isPlaying != player.isPlaying
                ) {
                    current.copy(
                        position = position,
                        duration = duration,
                        isPlaying = player.isPlaying
                    )
                } else {
                    current
                }
            }
        }
    }

    private fun updateNotification() {
        notificationHelper.setCurrentTrackUrl(
            url = playerState.value.currentTrack?.audioUrl,
            title = playerState.value.currentTrack?.title ?: "",
            imageUrl = playerState.value.currentTrack?.imageUrl
        )

        notificationHelper.updateNotification(playerState.value.isPlaying)
    }

    private fun handlePrevious() {
        if (playerState.value.currentPlaylistIndex > 0) {
            val newIndex = playerState.value.currentPlaylistIndex - 1
            exoPlayer?.let { player ->
                player.seekTo(newIndex, 0L)
                player.play()
            }

            _playerState.value = _playerState.value.copy(
                currentPlaylistIndex = newIndex,
                currentTrack = playerState.value.playlist[newIndex],
                isPlaying = true
            )
            updateNotification()
        }
    }

    private fun handleNext() {
        if (playerState.value.currentPlaylistIndex < playerState.value.playlist.size - 1) {
            val newIndex = playerState.value.currentPlaylistIndex + 1
            exoPlayer?.let { player ->
                player.seekTo(newIndex, 0L)
                player.play()
            }

            _playerState.value = _playerState.value.copy(
                currentPlaylistIndex = newIndex,
                currentTrack = playerState.value.playlist[newIndex],
                isPlaying = true
            )
            updateNotification()
        }
    }

    private fun handlePlay(intent: Intent) {
        try {
            if (exoPlayer == null) {
                initializePlayer()
            }
            val tracksData = intent.getStringExtra(TRACKS_DATA)
            if (tracksData != null) {
                handlePlaylist(intent, tracksData)
            } else {
                handleSingleTrack(intent)
            }

            startForegroundWithNotification()
        } catch (e: Exception) {
            stopSelf()
        }

    }

    private fun handlePlaylist(intent: Intent, tracksData: String) {
        try {
            val index = intent.getIntExtra(CURRENT_INDEX, 0)
            val (tracks, mediaItems) = parseTracksFromJson(tracksData)

            handleStop()

            exoPlayer?.let { player ->
                _playerState.update {
                    it.copy(
                        currentTrack = tracks[index],
                        playlist = tracks,
                        currentPlaylistIndex = index,
                        isPlaying = true,
                        isVisibilityPlayer = true
                    )
                }
                player.setMediaItems(mediaItems)
                player.prepare()
                player.seekTo(index, 0L)
                player.play()

                updateNotification()
            }
        } catch (e: Exception) {
            stopSelf()
        }
    }

    private fun handleSingleTrack(intent: Intent) {
        val url = intent.getStringExtra(URL) ?: return
        val position = intent.getLongExtra(EXO_PLAYER_POSITION, 0L)
        val title = intent.getStringExtra(CURRENT_TITLE) ?: ""
        val imageUrl = intent.getStringExtra(CURRENT_IMAGE_URL) ?: ""
        val trackId = intent.getStringExtra(CURRENT_ID) ?: ""

        handleStop()

        try {
            exoPlayer?.let { player ->
                val mediaItem = createMediaItem(url, title, imageUrl)
                val track = Track(
                    id = trackId,
                    title = title,
                    imageUrl = imageUrl,
                    audioUrl = url
                )
                _playerState.update {
                    it.copy(
                        currentTrack = track,
                        isPlaying = true,
                        isVisibilityPlayer = true
                    )
                }

                player.setMediaItem(mediaItem)
                player.prepare()
                player.seekTo(position)
                player.play()

                notificationHelper.setCurrentTrackUrl(url, title, imageUrl)
                notificationHelper.updateNotification(true)
            }
        } catch (e: Exception) {
            stopSelf()
        }
    }

    private fun parseTracksFromJson(tracksData: String): Pair<List<Track>, List<MediaItem>> {
        val jsonArray = JSONArray(tracksData)
        val tracks = mutableListOf<Track>()
        val mediaItem = mutableListOf<MediaItem>()

        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(i)
            tracks.add(createTrackFromJson(item))
            mediaItem.add(createMediaItemFromJson(item))
        }
        return tracks to mediaItem
    }

    private fun createTrackFromJson(item: JSONObject): Track {
        return Track(
            id = item.getString(CURRENT_ID),
            title = item.getString(CURRENT_TITLE),
            imageUrl = item.getString(CURRENT_IMAGE_URL),
            audioUrl = item.getString(URL)
        )
    }

    private fun createMediaItemFromJson(item: JSONObject): MediaItem {
        return createMediaItem(
            url = item.getString(URL),
            title = item.getString(CURRENT_TITLE),
            imageUrl = item.getString(CURRENT_IMAGE_URL)
        )
    }

    private fun createMediaItem(url: String, title: String, imageUrl: String?): MediaItem {
        return MediaItem.Builder()
            .setUri(url)
            .setRequestMetadata(
                MediaItem.RequestMetadata.Builder()
                    .setMediaUri(Uri.parse(url))
                    .build()
            )
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(title)
                    .setArtworkUri(imageUrl?.let { Uri.parse(it) })
                    .build()
            )
            .build()
    }

    private fun handlePause() {
        exoPlayer?.pause()
        _playerState.value = _playerState.value.copy(isPlaying = false)
        notificationHelper.updateNotification(false)
    }

    private fun handleResume() {
        exoPlayer?.play()
        _playerState.value = _playerState.value.copy(isPlaying = true)
        notificationHelper.updateNotification(true)
    }

    private fun observePlayerState(): Job = lifecycleScope.launch {
        exoPlayer?.let { player ->
            val listener = object : Player.Listener {
                override fun onEvents(player: Player, events: Player.Events) {
                    updatePlayerState()
                }

                override fun onPlayerError(error: PlaybackException) {
                    stopSelf()
                }
            }

            player.addListener(listener)
            try {
                while (isActive) {
                    updatePlayerState()
                    delay(500)
                }
            } finally {
                player.removeListener(listener)
            }
        }
    }

    fun isPlayerStopped(): Boolean {
        return when {
            exoPlayer == null -> true
            exoPlayer?.playbackState == Player.STATE_IDLE -> true
            exoPlayer?.playbackState == Player.STATE_ENDED -> true
            else -> false
        }
    }

    private fun handleStop() {
        try {
            exoPlayer?.let { player ->
                player.stop()
                player.clearMediaItems()
            }

            _playerState.value = PlayerState()

            stopForegroundCompat()
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            notificationHelper.removeNotification()
        } catch (e: Exception) {
            stopSelf()
        }
    }

    override fun onDestroy() {
        playerStateJob?.cancel()
        playerStateJob = null

        exoPlayer?.let { player ->
            player.stop()
            player.release()
        }
        exoPlayer = null

        notificationHelper.removeNotification()
        super.onDestroy()
    }

    inner class LocalBinder : Binder() {
        fun getService(): BackgroundService = this@BackgroundService
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return LocalBinder()
    }

    companion object {
        const val ACTION_PAUSE = "actionPause"
        const val ACTION_PLAY = "actionPlay"
        const val ACTION_START = "actionStart"
        const val ACTION_RESUME = "actionResume"
        const val ACTION_STOP = "actionStop"
        const val ACTION_NEXT = "actionNext"
        const val ACTION_PREVIOUS = "actionPrevious"
        const val ACTION_SEEK_TO = "actionSeekTo"

        const val URL = "url"
        const val TRACKS_DATA = "tracksData"
        const val CURRENT_ID = "currentId"
        const val EXO_PLAYER_POSITION = "position"
        const val CURRENT_INDEX = "currentIndex"
        const val CURRENT_TITLE = "currentTitle"
        const val CURRENT_IMAGE_URL = "currentImageUrl"
        const val SEEK_POSITION = "seekPosition"

        fun createIntent(context: Context, action: String, url: String? = null): Intent {
            return Intent(context, BackgroundService::class.java).apply {
                this.action = action
                url?.let { putExtra(URL, it) }
            }
        }
    }
}

fun Service.stopForegroundCompat(removeNotification: Boolean = true) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        stopForeground(
            if (removeNotification) {
                Service.STOP_FOREGROUND_REMOVE
            } else {
                Service.STOP_FOREGROUND_DETACH
            }
        )
    } else {
        @Suppress("DEPRECATION")
        stopForeground(removeNotification)
    }
}