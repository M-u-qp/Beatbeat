package com.muqp.feature_favorites.presentation.screen.adapter

import android.view.View
import com.bumptech.glide.Glide
import com.muqp.beatbeat.favorites.databinding.FavoriteTrackListItemBinding
import com.muqp.beatbeat.ui.R
import com.muqp.core_ui.adapters_for_recycler.RecyclerBindable
import com.muqp.feature_favorites.model.ItemTrack

class FavoriteTracksDataRecycler(
    private val item: ItemTrack,
    private val onClickTrack: (() -> Unit)? = null,
    private val onMenuClickListener: ((View, ItemTrack) -> Unit)? = null
) : RecyclerBindable {
    override fun bind(view: View) {
        val binding = FavoriteTrackListItemBinding.bind(view)

        with(binding) {
            trackName.text = item.name
            artistNameTv.text = item.artistName

            Glide.with(view.context)
                .load(item.image)
                .placeholder(R.drawable.icons8_track)
                .error(R.drawable.icons8_broken_image)
                .into(trackImageIv)

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