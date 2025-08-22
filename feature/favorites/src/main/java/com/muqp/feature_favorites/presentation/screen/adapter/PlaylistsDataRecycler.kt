package com.muqp.feature_favorites.presentation.screen.adapter

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.muqp.beatbeat.favorites.databinding.PlaylistItemBinding
import com.muqp.core_ui.adapters_for_recycler.RecyclerBindable
import com.muqp.core_ui.common.AnimationExpandedHelper
import com.muqp.core_utils.extensions.IntExt.getPluralForm
import com.muqp.feature_favorites.model.PlaylistUI
import java.lang.ref.WeakReference
import kotlin.math.abs
import com.muqp.beatbeat.ui.R as CoreUi

class PlaylistsDataRecycler(
    private val item: PlaylistUI,
    private val onDeletePlaylist: (Long) -> Unit,
    private val onPlaylistClicked: (Long) -> Unit,
    private val onBindTracks: (rv: RecyclerView, playlistId: Long?) -> Unit
) : RecyclerBindable {

    private inner class SwipeTouchListener: View.OnTouchListener {
        private var startX = 0f
        private var isSwiping = false
        private var maxSwipeDistance = 0f

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = event.rawX
                    isSwiping = false
                    maxSwipeDistance = v.width / 2f
                    return true
                }
                MotionEvent.ACTION_MOVE -> {
                    if (abs(event.rawX - startX) > 10f) {
                        isSwiping = true
                        v.parent.requestDisallowInterceptTouchEvent(true)
                    }
                    if (isSwiping) {
                        val translationX = (event.rawX - startX)
                            .coerceAtMost(0f)
                            .coerceAtLeast(-maxSwipeDistance)
                        v.translationX = translationX
                    }
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    if (isSwiping) {
                        handleSwipeEnd(v, maxSwipeDistance)
                        v.performClick()
                    }
                    isSwiping = false
                    return true
                }
                else -> return false
            }
        }
    }

    private fun handleSwipeEnd(view: View, maxSwipeDistance: Float) {
        if (abs(view.translationX) > maxSwipeDistance / 2) {
            view.animate()
                .translationX(-maxSwipeDistance)
                .setDuration(500L)
                .setInterpolator(DecelerateInterpolator())
                .start()
        } else {
            view.animate()
                .translationX(0f)
                .setDuration(500L)
                .setInterpolator(DecelerateInterpolator())
                .start()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun bind(view: View) {
        val binding = PlaylistItemBinding.bind(view)

        with(binding) {
            playlistTracksRv.layoutManager = LinearLayoutManager(view.context)
            playlistTitle.text = item.name
            playlistDescription.text = item.description
            tracksCountTv.text = item.trackCount.getPluralForm(
                one = view.context.getString(CoreUi.string.one_track),
                few = view.context.getString(CoreUi.string.few_track),
                many = view.context.getString(CoreUi.string.many_track)
            )

            containerPlay.setOnClickListener {
                item.id?.let { it1 -> onPlaylistClicked.invoke(it1) }
            }

            playlistUnwrap.setOnClickListener {
                lastOpenedRecyclerView?.get()?.takeIf { it != playlistTracksRv }?.let { oldRv ->
                    lastArrowView?.get()?.let { oldArrow ->
                        AnimationExpandedHelper.collapseViewWithAnimation(
                            view = oldRv,
                            arrowView = oldArrow
                        )
                    }
                }

                if (playlistTracksRv.isVisible) {
                    AnimationExpandedHelper.collapseViewWithAnimation(
                        view = playlistTracksRv,
                        animationResId = CoreUi.anim.expanded_slide_down,
                        arrowView = playlistUnwrap
                    )
                    clearLastReferences()
                } else {
                    AnimationExpandedHelper.expandViewWithAnimation(
                        view = playlistTracksRv,
                        animationResId = CoreUi.anim.expanded_slide_up,
                        arrowView = playlistUnwrap
                    )
                    onBindTracks(playlistTracksRv, item.id)
                    lastOpenedRecyclerView = WeakReference(playlistTracksRv)
                    lastArrowView = WeakReference(playlistUnwrap)
                }
            }

            playlistCard.setOnTouchListener(SwipeTouchListener())

            deletePlaylist.setOnClickListener {
                deletePlaylist.animate()
                    .setDuration(500L)
                    .alpha(0f)
                    .start()
                playlistCard.animate()
                    .translationX(-playlistCard.width.toFloat())
                    .setDuration(500L)
                    .setInterpolator(AccelerateInterpolator())
                    .withEndAction {
                        item.id?.let { it1 -> onDeletePlaylist.invoke(it1) }
                    }
                    .start()
            }
        }
    }

    override fun getId(): String {
        return item.id.toString()
    }

    companion object {
        private var lastOpenedRecyclerView: WeakReference<RecyclerView>? = null
        private var lastArrowView: WeakReference<ImageView>? = null

        private fun clearLastReferences() {
            lastOpenedRecyclerView?.clear()
            lastArrowView?.clear()
            lastOpenedRecyclerView = null
            lastArrowView = null
        }
    }
}