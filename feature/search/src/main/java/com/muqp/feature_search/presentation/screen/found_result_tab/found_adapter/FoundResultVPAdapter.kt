package com.muqp.feature_search.presentation.screen.found_result_tab.found_adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.muqp.feature_search.common.SearchQueryType
import com.muqp.feature_search.presentation.screen.found_result_tab.found_screens.found_albums.FoundAlbumsFragment
import com.muqp.feature_search.presentation.screen.found_result_tab.found_screens.found_artists.FoundArtistsFragment
import com.muqp.feature_search.presentation.screen.found_result_tab.found_screens.found_tracks.FoundTracksFragment

class FoundResultVPAdapter(
    fragment: Fragment,
    private val searchString: String
) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = SearchQueryType.entries.size

    override fun createFragment(position: Int): Fragment {
        return when (SearchQueryType.fromPosition(position)) {
            SearchQueryType.TRACKS -> FoundTracksFragment().apply {
                arguments = Bundle().apply {
                    putString(SEARCH_STRING, searchString)
                }
            }

            SearchQueryType.ALBUMS -> FoundAlbumsFragment().apply {
                arguments = Bundle().apply {
                    putString(SEARCH_STRING, searchString)
                }
            }

            SearchQueryType.ARTISTS -> FoundArtistsFragment().apply {
                arguments = Bundle().apply {
                    putString(SEARCH_STRING, searchString)
                }
            }
        }
    }

    companion object {
        const val SEARCH_STRING = "searchString"
    }
}