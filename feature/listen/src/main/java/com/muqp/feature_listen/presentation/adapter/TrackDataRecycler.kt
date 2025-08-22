package com.muqp.feature_listen.presentation.adapter

import android.view.View
import com.bumptech.glide.Glide
import com.muqp.beatbeat.listen.databinding.ListenTrackListItemBinding
import com.muqp.beatbeat.ui.R
import com.muqp.core_ui.adapters_for_recycler.RecyclerBindable
import com.muqp.core_ui.common.ToggleFavorite
import com.muqp.feature_listen.model.ItemTrackUI

class TrackDataRecycler(
    private val item: ItemTrackUI,
    private val isFavoriteInitial: Boolean,
    private val onFavoriteClick: (onResultClicked: (Boolean) -> Unit) -> Unit,
    private val onMenuClickListener: ((View, ItemTrackUI) -> Unit)? = null,
    private val onClickTrack: (() -> Unit)? = null
) : RecyclerBindable {
    override fun bind(view: View) {
        val binding = ListenTrackListItemBinding.bind(view)
        with(binding) {
            artistNameTv.text = item.artistName
            trackName.text = item.name

            Glide.with(view.context)
                .load(item.image)
                .placeholder(R.drawable.icons8_track)
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