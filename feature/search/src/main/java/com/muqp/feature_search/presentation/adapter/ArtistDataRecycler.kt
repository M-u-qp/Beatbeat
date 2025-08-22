package com.muqp.feature_search.presentation.adapter

import android.view.View
import com.bumptech.glide.Glide
import com.muqp.beatbeat.ui.R
import com.muqp.core_ui.adapters_for_recycler.RecyclerBindable
import com.muqp.beatbeat.search.databinding.ArtistListItemBinding
import com.muqp.core_ui.common.ToggleFavorite
import com.muqp.feature_search.model.ItemArtistUI

class ArtistDataRecycler(
    private val item: ItemArtistUI,
    private val isFavoriteInitial: Boolean,
    private val onFavoriteClick: (onResultClicked: (Boolean) -> Unit) -> Unit,
    private val onArtistClick: () -> Unit,
    private val onMenuClickListener: ((View, ItemArtistUI) -> Unit)? = null
) : RecyclerBindable {
    override fun bind(view: View) {
        val binding = ArtistListItemBinding.bind(view)
        with(binding) {
            artistNameTv.text = item.name

            Glide.with(view.context)
                .load(item.image)
                .placeholder(R.drawable.icons8_artist)
                .error(R.drawable.icons8_broken_image)
                .into(artistImageIv)

            ToggleFavorite.updateColorFavoriteIcon(
                artistFavoriteIv,
                isFavoriteInitial,
                trueColor = R.color.genre_pop,
                falseColor = R.color.black
            )

            artistClickCl.setOnClickListener {
                onArtistClick.invoke()
            }

            artistFavoriteIv.setOnClickListener {
                onFavoriteClick { newFavoriteState ->
                    ToggleFavorite.updateColorFavoriteIcon(
                        artistFavoriteIv,
                        newFavoriteState,
                        trueColor = R.color.genre_pop,
                        falseColor = R.color.black
                    )
                }
            }

            artistDotsIv.setOnClickListener { v ->
                onMenuClickListener?.invoke(v,item)
            }
        }
    }

    override fun getId(): String {
        return item.id
    }
}