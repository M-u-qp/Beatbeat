package com.muqp.beatbeat.details.presentation.screen.adapter

import android.view.View
import com.bumptech.glide.Glide
import com.muqp.beatbeat.details.databinding.AlbumTracksListItemBinding
import com.muqp.beatbeat.details.model.ItemTrackUI
import com.muqp.beatbeat.ui.R
import com.muqp.core_ui.adapters_for_recycler.RecyclerBindable
import com.muqp.core_ui.common.ToggleFavorite

class AlbumTracksDataRecycler(
    private val item: ItemTrackUI,
    private val isFavoriteInitial: Boolean,
    private val onFavoriteClick: (onResultClicked: (Boolean) -> Unit) -> Unit,
    private val onClickTrack: (() -> Unit)? = null,
    private val onMenuClickListener: ((View, ItemTrackUI) -> Unit)? = null
) : RecyclerBindable {
    override fun bind(view: View) {
        val binding = AlbumTracksListItemBinding.bind(view)
        with(binding) {
            trackName.text = item.name

            Glide.with(view.context)
                .load(R.drawable.icons8_track)
                .placeholder(R.drawable.icons8_beatbeat)
                .error(R.drawable.icons8_broken_image)
                .into(trackImageIv)

            ToggleFavorite.updateColorFavoriteIcon(
                trackFavoriteIv,
                isFavoriteInitial,
                trueColor = R.color.genre_pop,
                falseColor = R.color.black
            )

            trackFavoriteIv.setOnClickListener {
                onFavoriteClick { newFavoriteState ->
                    ToggleFavorite.updateColorFavoriteIcon(
                        trackFavoriteIv,
                        newFavoriteState,
                        trueColor = R.color.genre_pop,
                        falseColor = R.color.black
                    )
                }
            }

            trackDotsIv.setOnClickListener { v ->
                onMenuClickListener?.invoke(v, item)
            }

            clickTrackLl.setOnClickListener {
                onClickTrack?.invoke()
            }
        }
    }

    override fun getId(): String {
        return item.id
    }
}