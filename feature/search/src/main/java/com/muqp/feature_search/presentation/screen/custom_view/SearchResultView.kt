package com.muqp.feature_search.presentation.screen.custom_view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.muqp.core_ui.adapters_for_recycler.CommonAdapter
import com.muqp.feature_search.common.SearchQueryType
import com.muqp.beatbeat.search.databinding.ViewSearchResultBinding

class SearchResultView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private val binding: ViewSearchResultBinding = ViewSearchResultBinding.inflate(
        LayoutInflater.from(context), this, true
    )

    private lateinit var artistAdapter: CommonAdapter
    private lateinit var albumAdapter: CommonAdapter
    private lateinit var trackAdapter: CommonAdapter

    fun initAdapters(
        artistAdapter: CommonAdapter,
        albumAdapter: CommonAdapter,
        trackAdapter: CommonAdapter
    ) {
        this.artistAdapter = artistAdapter
        this.albumAdapter = albumAdapter
        this.trackAdapter = trackAdapter

        with(binding) {
            artistsRv.layoutManager = LinearLayoutManager(context)
            artistsRv.adapter = artistAdapter
            albumsRv.layoutManager = LinearLayoutManager(context)
            albumsRv.adapter = albumAdapter
            tracksRv.layoutManager = LinearLayoutManager(context)
            tracksRv.adapter = trackAdapter
        }
    }

    fun isEmptyRecycler(
        showOrHide: Boolean,
        searchQueryType: SearchQueryType
    ) {
        val visibilityView = if (showOrHide) {
            View.GONE
        } else {
            View.VISIBLE
        }
        when (searchQueryType) {
            SearchQueryType.TRACKS -> {
                binding.tracksLl.visibility = visibilityView
            }

            SearchQueryType.ALBUMS -> {
                binding.albumsLl.visibility = visibilityView
            }

            SearchQueryType.ARTISTS -> {
                binding.artistsLl.visibility = visibilityView
            }
        }
    }

    fun onClickAllFoundResults(navigate: (positionTab: Int) -> Unit) {
        binding.allTrackIv.setOnClickListener { navigate.invoke(SearchQueryType.TRACKS.position) }
        binding.allAlbumIv.setOnClickListener { navigate.invoke(SearchQueryType.ALBUMS.position) }
        binding.allArtistIv.setOnClickListener { navigate.invoke(SearchQueryType.ARTISTS.position) }
    }

    fun show() {
        visibility = View.VISIBLE
    }

    fun hide() {
        visibility = View.GONE
    }

    fun setFoundDataInAdapter(
        searchQueryType: SearchQueryType,
        submitAction: (CommonAdapter) -> Unit
    ) {
        val adapter = when (searchQueryType) {
            SearchQueryType.TRACKS -> trackAdapter
            SearchQueryType.ALBUMS -> albumAdapter
            SearchQueryType.ARTISTS -> artistAdapter
        }
        submitAction(adapter)
    }
}