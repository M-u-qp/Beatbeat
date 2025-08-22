package com.muqp.beatbeat.details.presentation.screen.adapter

import android.view.View
import com.bumptech.glide.Glide
import com.muqp.beatbeat.details.databinding.ArtistAlbumsItemListBinding
import com.muqp.beatbeat.details.model.ItemAlbumUI
import com.muqp.beatbeat.ui.R
import com.muqp.core_ui.adapters_for_recycler.RecyclerBindable

class ArtistAlbumsDataRecycler(
    private val item: ItemAlbumUI,
    private val onClick: () -> Unit
) : RecyclerBindable {
    override fun bind(view: View) {
        val binding = ArtistAlbumsItemListBinding.bind(view)
        with(binding) {
            albumNameTv.text = item.name
            artistNameTv.text = item.artistName

            cvForAlbumImage.setOnClickListener { onClick.invoke() }

            Glide.with(view.context)
                .load(item.image)
                .placeholder(R.drawable.icons8_artist)
                .error(R.drawable.icons8_broken_image)
                .into(albumImageIv)
        }
    }

    override fun getId(): String {
        return item.id
    }
}