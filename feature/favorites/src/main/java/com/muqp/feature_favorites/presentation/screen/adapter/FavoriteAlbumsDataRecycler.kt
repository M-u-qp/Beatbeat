package com.muqp.feature_favorites.presentation.screen.adapter

import android.view.View
import com.bumptech.glide.Glide
import com.muqp.beatbeat.favorites.databinding.FavoriteAlbumListItemBinding
import com.muqp.beatbeat.ui.R
import com.muqp.core_ui.adapters_for_recycler.RecyclerBindable
import com.muqp.feature_favorites.model.ItemAlbum

class FavoriteAlbumsDataRecycler(
    private val item: ItemAlbum,
    private val onAlbumClick: () -> Unit,
    private val onMenuClickListener: ((View, ItemAlbum) -> Unit)? = null
) : RecyclerBindable {
    override fun bind(view: View) {
        val binding = FavoriteAlbumListItemBinding.bind(view)

        with(binding) {
            albumNameTv.text = item.name
            artistNameTv.text = item.artistName
            albumReleaseTv.text = item.releaseDate

            Glide.with(view.context)
                .load(item.image)
                .placeholder(R.drawable.icons8_album)
                .error(R.drawable.icons8_broken_image)
                .into(albumImageIv)

            albumClickCl.setOnClickListener {
                onAlbumClick.invoke()
            }

            albumDotsIv.setOnClickListener { v ->
                onMenuClickListener?.invoke(v, item)
            }
        }
    }

    override fun getId(): String {
        return item.id
    }
}