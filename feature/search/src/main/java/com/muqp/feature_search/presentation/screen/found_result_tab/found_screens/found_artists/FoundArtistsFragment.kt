package com.muqp.feature_search.presentation.screen.found_result_tab.found_screens.found_artists

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.muqp.beatbeat.search.R
import com.muqp.beatbeat.search.databinding.FragmentArtistsFoundBinding
import com.muqp.core_ui.adapters_for_recycler.AdapterFactory
import com.muqp.core_ui.bottom_sheet.DotsBottomSheetMenu
import com.muqp.core_ui.bottom_sheet.DotsBottomSheetMenu.showDotsMenu
import com.muqp.core_ui.common.ToggleFavorite
import com.muqp.core_ui.custom_view.LoadingView
import com.muqp.core_utils.extensions.ContextExt.openInBrowser
import com.muqp.core_utils.extensions.ContextExt.shareUrl
import com.muqp.core_utils.extensions.NavigationExt.navigate
import com.muqp.core_ui.paging.HandlePaginationLoadState
import com.muqp.core_utils.has_dependencies.HasDependencies
import com.muqp.feature_search.model.ItemArtistUI
import com.muqp.feature_search.presentation.screen.found_result_tab.found_adapter.FoundResultVPAdapter.Companion.SEARCH_STRING
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FoundArtistsFragment : Fragment() {
    private var _binding: FragmentArtistsFoundBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: FoundArtistsViewModel by viewModels { viewModelFactory }

    private lateinit var loadingView: LoadingView

    private val artistAdapter by lazy { AdapterFactory.createPagingAdapter(R.layout.artist_list_item) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArtistsFoundBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingView = LoadingView(requireContext())
        binding.root.addView(loadingView)

        val dependencies = (requireActivity() as HasDependencies).getViewModelFactory()
        viewModelFactory = dependencies.provideViewModelFactory()

        initListeners()
    }

    private fun initListeners() = with(binding) {
        foundArtistsRv.layoutManager = LinearLayoutManager(context)
        foundArtistsRv.adapter = artistAdapter

        arguments?.getString(SEARCH_STRING)?.let {
            viewModel.loadSearchResultToTrack(it)
        }

        viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collectLatest { pagingData ->
                    if (pagingData != null) {
                        val pagingArtistData =
                            viewModel.loadArtistDataRecycler(
                                pagingData = pagingData,
                                onArtistClick = { artist ->
                                    navigate(
                                        R.id.action_foundResultMainFragment_to_artistDetailsFragment,
                                        stringData = Gson().toJson(artist)
                                    )
                                }
                            ) { view, item ->
                                showArtistActionsMenu(view.context, item)
                            }
                        artistAdapter.submitData(pagingArtistData)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
            viewModel.errorMessage.collect { errorMessage ->
                errorMessage?.let {
                    showErrorMessageToast(errorMessage)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
            viewModel.pagingLoadState.collectLatest { isLoading ->
                if (isLoading) {
                    loadingView.show()
                } else {
                    loadingView.hide()
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
            artistAdapter.loadStateFlow.collectLatest { loadState ->
                HandlePaginationLoadState.handlePaginationLoadState(loadState) { isLoading ->
                    viewModel.updatePagingLoadState(isLoading)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
            viewModel.favoriteSideEffect.collect { result ->
                result?.let {
                    val message = when (result.favoriteResultType) {
                        ToggleFavorite.FavoriteResultType.ADDED -> {
                            getString(com.muqp.beatbeat.ui.R.string.artist_added_to_favorites)
                        }

                        ToggleFavorite.FavoriteResultType.REMOVED -> {
                            getString(com.muqp.beatbeat.ui.R.string.artist_removed_to_favorites)

                        }
                    }
                    showFavoriteToast(message)
                }
            }
        }
    }

    private fun showArtistActionsMenu(context: Context, artist: ItemArtistUI) {
        context.showDotsMenu(
            title = artist.name,
            imageUrl = artist.image,
            actions = listOf(
                DotsBottomSheetMenu.BottomSheetAction(
                    iconRes = com.muqp.beatbeat.ui.R.drawable.icons8_browsable,
                    title = getString(com.muqp.beatbeat.ui.R.string.browsable),
                    action = { context.openInBrowser(artist.shareUrl) }
                ),
                DotsBottomSheetMenu.BottomSheetAction(
                    iconRes = com.muqp.beatbeat.ui.R.drawable.icons8_share_v2,
                    title = getString(com.muqp.beatbeat.ui.R.string.to_share),
                    action = { context.shareUrl(artist.shareUrl, artist.name) }
                )
            )
        )
    }

    private fun showErrorMessageToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        viewModel.resetErrorMessage()
    }

    private fun showFavoriteToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onStop() {
        super.onStop()
        viewModel.resetFavoriteSideEffect()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.root.removeView(loadingView)
        _binding = null
    }
}