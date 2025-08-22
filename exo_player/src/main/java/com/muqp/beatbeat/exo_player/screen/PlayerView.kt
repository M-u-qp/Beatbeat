package com.muqp.beatbeat.exo_player.screen

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.TimeBar
import com.bumptech.glide.Glide
import com.muqp.beatbeat.exo_player.PlayerManager
import com.muqp.beatbeat.exo_player.R
import com.muqp.beatbeat.exo_player.databinding.PlayerMotionBinding
import com.muqp.beatbeat.exo_player.model.Track
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.lang.ref.WeakReference
import java.util.Locale

@UnstableApi
class PlayerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MotionLayout(context, attrs, defStyleAttr) {

    private val contextRef = WeakReference(context)

    private val binding: PlayerMotionBinding = PlayerMotionBinding.inflate(
        LayoutInflater.from(contextRef.get()), this, true
    )

    var onFullscreenChanged: ((isFullscreen: Boolean) -> Unit)? = null

    private lateinit var playerManager: PlayerManager
    private var lastImageUrl: String? = null
    private var isFirstShowMiniPlayer = true

    fun initPlayerView(playerManager: PlayerManager, lifecycleOwner: LifecycleOwner) {
        this.playerManager = playerManager

        setupControls()
        setupStateObservers(lifecycleOwner)
    }

    private fun setupControls() = with(binding) {
        miniPlayerIncl.miniPlayBtn.setOnClickListener {
            playerManager.togglePlayPause()
        }

        miniPlayerIncl.miniPrevBtn.setOnClickListener {
            playerManager.seekToPrevious()
        }

        miniPlayerIncl.miniNextBtn.setOnClickListener {
            playerManager.seekToNext()
        }

        miniPlayerIncl.miniStopBtn.setOnClickListener {
            startAnimation(
                AnimationUtils.loadAnimation(
                    contextRef.get(),
                    R.anim.expanded_slide_down
                )
            )
            playerMotionLayout.transitionToState(R.id.mini_state)
            playerManager.stopTrack()
            isFirstShowMiniPlayer = true
        }

        miniPlayerIncl.miniCoverIv.setOnClickListener {
            playerMotionLayout.transitionToState(R.id.full_state)
            onFullscreenChanged?.invoke(true)
        }

        fullPlayerIncl.fullCoverIv.setOnClickListener {
            playerMotionLayout.transitionToState(R.id.mini_state)
            onFullscreenChanged?.invoke(false)
        }

        fullPlayerIncl.fullPlay.setOnClickListener {
            playerManager.togglePlayPause()
        }

        fullPlayerIncl.fullStop.setOnClickListener {
            playerMotionLayout.transitionToState(R.id.mini_state)
            playerManager.stopTrack()
            isFirstShowMiniPlayer = true
        }

        fullPlayerIncl.fullNext.setOnClickListener {
            playerManager.seekToNext()
        }

        fullPlayerIncl.fullPrev.setOnClickListener {
            playerManager.seekToPrevious()
        }

        miniPlayerIncl.miniProgress.addListener(object : TimeBar.OnScrubListener {
            override fun onScrubStart(timeBar: TimeBar, position: Long) {}
            override fun onScrubMove(timeBar: TimeBar, position: Long) {}
            override fun onScrubStop(timeBar: TimeBar, position: Long, canceled: Boolean) {
                playerManager.seekTo(position)
            }
        })

        fullPlayerIncl.fullProgress.addListener(object : TimeBar.OnScrubListener {
            override fun onScrubStart(timeBar: TimeBar, position: Long) {}
            override fun onScrubMove(timeBar: TimeBar, position: Long) {
                fullPlayerIncl.fullPosition.text = position.toTrackDurationString()
            }

            override fun onScrubStop(timeBar: TimeBar, position: Long, canceled: Boolean) {
                playerManager.seekTo(position)
            }
        })
    }

    private fun setupStateObservers(lifecycleOwner: LifecycleOwner) = with(binding) {
        playerManager.playerUiEvents
            .flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { event ->
                when (event) {
                    is PlayerManager.PlayerUiEvent.ShowPlayer -> {
                        if (isFirstShowMiniPlayer) {
                            startAnimation(
                                AnimationUtils.loadAnimation(
                                    contextRef.get(),
                                    R.anim.expanded_slide_up
                                )
                            )
                            isFirstShowMiniPlayer = false
                        }
                        visibility = View.VISIBLE
                    }

                    is PlayerManager.PlayerUiEvent.HidePlayer -> {
                        visibility = View.GONE
                    }
                }
            }
            .launchIn(lifecycleOwner.lifecycleScope)

        playerManager.currentTrack
            .flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { track ->
                updateTrackInfo(track)
                updateCoverImage(track?.imageUrl)
            }
            .launchIn(lifecycleOwner.lifecycleScope)

        playerManager.isPlaying
            .flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { isPlaying ->
                updatePlayButton(isPlaying)
            }
            .launchIn(lifecycleOwner.lifecycleScope)

        playerManager.playbackPosition
            .flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { position ->
                miniPlayerIncl.miniProgress.setPosition(position)
                fullPlayerIncl.fullProgress.setPosition(position)
                fullPlayerIncl.fullPosition.text = position.toTrackDurationString()
            }
            .launchIn(lifecycleOwner.lifecycleScope)

        playerManager.playbackDuration
            .flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { duration ->
                miniPlayerIncl.miniProgress.setDuration(duration)
                fullPlayerIncl.fullProgress.setDuration(duration)
                fullPlayerIncl.fullDuration.text = duration.toTrackDurationString()
            }
            .launchIn(lifecycleOwner.lifecycleScope)

        playerManager.currentPlaylistIndex
            .flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { index ->
                val isEnableBtn = index != -1
                val iconColor = contextRef.get()?.getColor(
                    if (isEnableBtn) {
                        R.color.light_brown
                    } else {
                        R.color.black
                    }
                )

                listOf(
                    miniPlayerIncl.miniNextBtn to miniPlayerIncl.miniNextIc,
                    fullPlayerIncl.fullNext to fullPlayerIncl.fullNextIc,
                    miniPlayerIncl.miniPrevBtn to miniPlayerIncl.miniPrevIc,
                    fullPlayerIncl.fullPrev to fullPlayerIncl.fullPrevIc
                ).forEach { (btn, ic) ->
                    btn.isEnabled = isEnableBtn
                    if (iconColor != null) {
                        ic.setBackgroundColor(iconColor)
                    }
                }
            }
            .launchIn(lifecycleOwner.lifecycleScope)
    }

    private fun updateTrackInfo(track: Track?) {
        binding.fullPlayerIncl.fullTitleTv.text = track?.title
        binding.miniPlayerIncl.miniTitleTv.text = track?.title
    }

    private fun updateCoverImage(imageUrl: String?) {
        if (imageUrl != lastImageUrl) {
            contextRef.get()?.let {
                Glide.with(it)
                    .load(imageUrl)
                    .placeholder(R.drawable.icons8_track)
                    .error(R.drawable.icons8_broken_image)
                    .into(binding.miniPlayerIncl.miniCoverIv)

                Glide.with(it)
                    .load(imageUrl)
                    .placeholder(R.drawable.icons8_track)
                    .error(R.drawable.icons8_broken_image)
                    .into(binding.fullPlayerIncl.fullCoverIv)
            }
            lastImageUrl = imageUrl
        }
    }

    private fun updatePlayButton(isPlaying: Boolean) {
        val icon = if (isPlaying) {
            R.drawable.icons8_pause
        } else {
            R.drawable.icons8_play_v2
        }
        binding.miniPlayerIncl.miniPlayIc.setImageResource(icon)
        binding.fullPlayerIncl.fullPlayIc.setImageResource(icon)
    }
}

fun Long.toTrackDurationString(): String {
    return when {
        this == C.TIME_UNSET -> "LIVE"
        this <= 0 -> "--:--"
        else -> {
            val seconds = (this / 1000).toInt()
            String.format(
                Locale.getDefault(),
                "%02d:%02d",
                seconds / 60,
                seconds % 60
            )
        }
    }
}