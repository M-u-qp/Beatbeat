package com.muqp.feature_favorites.presentation.screen.adapter

import android.view.View
import com.bumptech.glide.Glide
import com.muqp.beatbeat.favorites.databinding.FavoriteArtistListItemBinding
import com.muqp.beatbeat.ui.R
import com.muqp.core_ui.adapters_for_recycler.RecyclerBindable
import com.muqp.feature_favorites.model.ItemArtist

class FavoriteArtistsDataRecycler(
    private val item: ItemArtist,
    private val onArtistClick: () -> Unit,
    private val onMenuClickListener: ((View, ItemArtist) -> Unit)? = null
) : RecyclerBindable {
    override fun bind(view: View) {
        val binding = FavoriteArtistListItemBinding.bind(view)

        with(binding) {
            artistNameTv.text = item.name

            Glide.with(view.context)
                .load(item.image)
                .placeholder(R.drawable.icons8_artist)
                .error(R.drawable.icons8_broken_image)
                .into(artistImageIv)

            artistClickCl.setOnClickListener {
                onArtistClick.invoke()
            }

            artistDotsIv.setOnClickListener { v ->
                onMenuClickListener?.invoke(v, item)
            }
        }
    }

    override fun getId(): String {
        return item.id
    }
}