package com.muqp.feature_search.presentation.screen.found_result_tab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.muqp.core_utils.extensions.NavigationExt.navigationDataInt
import com.muqp.core_utils.extensions.NavigationExt.navigationDataString
import com.muqp.feature_search.common.SearchQueryType
import com.muqp.beatbeat.search.databinding.FragmentMainResultFoundBinding
import com.muqp.feature_search.presentation.screen.found_result_tab.found_adapter.FoundResultVPAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.muqp.beatbeat.ui.R as CoreUi

class FoundResultMainFragment : Fragment() {
    private var _binding: FragmentMainResultFoundBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainResultFoundBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
    }

    private fun initListeners() = with(binding) {
        val positionTab = navigationDataInt ?: 0
        val searchString = navigationDataString
        val viewPagerAdapter = searchString?.let {
            FoundResultVPAdapter(
                fragment = this@FoundResultMainFragment,
                searchString = it
            )
        }
        foundResultVp2.adapter = viewPagerAdapter
        foundResultVp2.setCurrentItem(positionTab, false)

        TabLayoutMediator(foundResultTab, foundResultVp2) { tab, position ->
            tab.text = when (SearchQueryType.fromPosition(position)) {
                SearchQueryType.TRACKS -> requireContext().getString(CoreUi.string.tracks)
                SearchQueryType.ALBUMS -> requireContext().getString(CoreUi.string.albums)
                SearchQueryType.ARTISTS -> requireContext().getString(CoreUi.string.artists)
            }
        }.attach()
    }
}