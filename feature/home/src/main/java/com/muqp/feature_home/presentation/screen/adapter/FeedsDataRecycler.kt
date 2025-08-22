package com.muqp.feature_home.presentation.screen.adapter

import android.text.util.Linkify
import android.view.View
import com.bumptech.glide.Glide
import com.muqp.core_ui.adapters_for_recycler.RecyclerBindable
import com.muqp.core_utils.extensions.StringExt.toHttps
import com.muqp.beatbeat.home.databinding.FeedListItemBinding
import com.muqp.feature_home.model.FeedItemUI
import com.muqp.beatbeat.ui.R as CoreUi

class FeedsDataRecycler(
    private val item: FeedItemUI,
    private val onMenuClickListener: ((View, FeedItemUI) -> Unit)? = null
) : RecyclerBindable {
    override fun bind(view: View) {
        val binding = FeedListItemBinding.bind(view)
        with(binding) {
            feedTitleTv.text = item.title.en
            feedTextTv.text = item.text.en
            feedDateTv.text = item.dateStart

            when (item.type) {
                TypeMusicFeeds.ARTIST.value -> {
                    feedIconTypeIv.setImageResource(CoreUi.drawable.icons8_artist)
                }

                TypeMusicFeeds.TRACK.value -> {
                    feedIconTypeIv.setImageResource(CoreUi.drawable.icons8_track)
                }

                TypeMusicFeeds.PLAYLIST.value -> {
                    feedIconTypeIv.setImageResource(CoreUi.drawable.icons8_playlist)
                }

                TypeMusicFeeds.ALBUM.value -> {
                    feedIconTypeIv.setImageResource(CoreUi.drawable.icons8_album)
                }

                TypeMusicFeeds.NEWS.value -> {
                    feedIconTypeIv.setImageResource(CoreUi.drawable.icons8_news)
                }
            }

            feedLinkTv.text = item.link.toHttps()
            Linkify.addLinks(feedLinkTv, Linkify.WEB_URLS)

            Glide.with(view.context)
                .load(item.images.size315X111)
                .placeholder(CoreUi.drawable.icons8_beatbeat)
                .error(CoreUi.drawable.icons8_broken_image)
                .into(feedImageIv)

            feedDotsIv.setOnClickListener { v ->
                onMenuClickListener?.invoke(v, item)
            }
        }
    }

    private enum class TypeMusicFeeds(
        val value: String
    ) {
        ARTIST("artist"),
        TRACK("track"),
        PLAYLIST("playlist"),
        ALBUM("album"),
        NEWS("news")
    }

    override fun getId(): String {
        return item.id
    }
}