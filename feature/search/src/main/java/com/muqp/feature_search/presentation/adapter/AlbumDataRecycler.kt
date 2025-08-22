package com.muqp.feature_search.presentation.adapter

import android.view.View
import com.bumptech.glide.Glide
import com.muqp.beatbeat.ui.R
import com.muqp.core_ui.adapters_for_recycler.RecyclerBindable
import com.muqp.beatbeat.search.databinding.AlbumListItemBinding
import com.muqp.core_ui.common.ToggleFavorite
import com.muqp.feature_search.model.ItemAlbumUI

class AlbumDataRecycler(
    private val item: ItemAlbumUI,
    private val isFavoriteInitial: Boolean,
    private val onFavoriteClick: (onResultClicked: (Boolean) -> Unit) -> Unit,
    private val onAlbumClick: () -> Unit,
    private val onMenuClickListener: ((View, ItemAlbumUI) -> Unit)? = null
) : RecyclerBindable {
    override fun bind(view: View) {
        val binding = AlbumListItemBinding.bind(view)
        with(binding) {
            artistNameTv.text = item.artistName
            albumNameTv.text = item.name
            albumReleaseTv.text = item.releaseDate

            Glide.with(view.context)
                .load(item.image)
                .placeholder(R.drawable.icons8_album)
                .error(R.drawable.icons8_broken_image)
                .into(albumImageIv)

            ToggleFavorite.updateColorFavoriteIcon(
                albumFavoriteIv,
                isFavoriteInitial,
                trueColor = R.color.genre_pop,
                falseColor = R.color.black
            )

            albumClickCl.setOnClickListener {
                onAlbumClick.invoke()
            }

            albumFavoriteIv.setOnClickListener {
                onFavoriteClick { newFavoriteState ->
                    ToggleFavorite.updateColorFavoriteIcon(
                        albumFavoriteIv,
                        newFavoriteState,
                        trueColor = R.color.genre_pop,
                        falseColor = R.color.black
                    )
                }
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