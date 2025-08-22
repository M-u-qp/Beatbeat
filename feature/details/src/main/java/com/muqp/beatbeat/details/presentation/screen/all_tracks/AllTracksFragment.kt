package com.muqp.beatbeat.details.presentation.screen.all_tracks

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
import com.muqp.beatbeat.details.R
import com.muqp.beatbeat.details.databinding.FragmentAllTracksBinding
import com.muqp.beatbeat.details.model.ItemTrackUI
import com.muqp.beatbeat.details.presentation.screen.artist_details.ArtistDetailsFragment.Companion.CREATE_PLAYLIST
import com.muqp.core_ui.adapters_for_recycler.AdapterFactory
import com.muqp.core_ui.bottom_sheet.DotsBottomSheetMenu
import com.muqp.core_ui.bottom_sheet.DotsBottomSheetMenu.showDotsMenu
import com.muqp.core_ui.common.ToggleFavorite.FavoriteResultType
import com.muqp.core_ui.custom_view.LoadingView
import com.muqp.core_ui.dialog.PlaylistSelectionDialog
import com.muqp.core_ui.downloader.downloadMediaFile
import com.muqp.core_ui.paging.HandlePaginationLoadState
import com.muqp.core_utils.extensions.NavigationExt.navigate
import com.muqp.core_utils.extensions.NavigationExt.navigateBack
import com.muqp.core_utils.extensions.NavigationExt.navigationDataInt
import com.muqp.core_utils.has_dependencies.HasDependencies
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.muqp.beatbeat.ui.R as CoreUi

class AllTracksFragment : Fragment() {
    private var _binding: FragmentAllTracksBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: AllTracksViewModel by viewModels { viewModelFactory }

    private lateinit var loadingView: LoadingView

    private val tracksAdapter by lazy {
        AdapterFactory.createPagingAdapter(
            layoutResId = R.layout.album_tracks_list_item
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllTracksBinding.inflate(inflater, container, false)
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
        allTracksRv.layoutManager = LinearLayoutManager(context)
        allTracksRv.adapter = tracksAdapter

        navigationDataInt?.let {
            viewModel.loadAllPopularTracksByArtist(it)
        }

        viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
            viewModel.errorMessage.collect { errorMessage ->
                errorMessage?.let {
                    showErrorMessageToast(errorMessage)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collectLatest { pagingData ->
                    if (pagingData != null) {
                        val pagingTracksData =
                            viewModel.loadPopularArtistTracksDataRecycler(
                                pagingData = pagingData,
                                onClickTrack = { track -> viewModel.onTrackClicked(track) }
                            ) { view, item ->
                                showTrackActionsMenu(
                                    context = view.context,
                                    track = item
                                )
                            }
                        tracksAdapter.submitData(pagingTracksData)
                    }
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
            tracksAdapter.loadStateFlow.collectLatest { loadState ->
                HandlePaginationLoadState.handlePaginationLoadState(loadState) { isLoading ->
                    viewModel.updatePagingLoadState(isLoading)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
            viewModel.favoriteSideEffect.collect { result ->
                result?.let {
                    val message = when (result.favoriteResultType) {
                        FavoriteResultType.ADDED -> {
                            getString(
                                CoreUi.string.track_added_to_favorites
                            )
                        }

                        FavoriteResultType.REMOVED -> {
                            getString(
                                CoreUi.string.track_removed_to_favorites
                            )
                        }
                    }
                    showFavoriteToast(message)
                }
            }
        }

        backIconIv.setOnClickListener {
            navigateBack()
        }
    }

    private fun showTrackActionsMenu(
        context: Context,
        track: ItemTrackUI
    ) {
        context.showDotsMenu(
            title = track.name,
            actions = listOf(
                DotsBottomSheetMenu.BottomSheetAction(
                    iconRes = CoreUi.drawable.icons8_download,
                    title = getString(CoreUi.string.download_track),
                    action = {
                        viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
                            context.downloadMediaFile(
                                track.audioDownload,
                                track.name
                            )
                        }
                    }
                ),
                DotsBottomSheetMenu.BottomSheetAction(
                    iconRes = CoreUi.drawable.icons8_playlist,
                    title = getString(CoreUi.string.add_to_playlist),
                    action = {
                        viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
                            viewModel.getPlaylists()
                            viewModel.playlists.collect { playlists ->
                                val dialogItems = playlists.map { item ->
                                    PlaylistSelectionDialog.PlaylistSelectionItem(
                                        id = item.id ?: 0L,
                                        name = item.name,
                                        description = item.description
                                    )
                                }
                                PlaylistSelectionDialog(
                                    context = requireContext(),
                                    playlists = dialogItems
                                ).apply {
                                    onPlaylistSelected = { selectedItem ->
                                        viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
                                            viewModel.addTrackToPlaylist(selectedItem.id, track)

                                            showFavoriteToast("${track.name} ${getString(CoreUi.string.added_to)} ${selectedItem.name}")
                                        }
                                    }

                                    onCreatePlaylistClicked = {
                                        navigate(
                                            actionId = R.id.action_allTracksFragment_to_favoritesFragment,
                                            stringData = CREATE_PLAYLIST
                                        )
                                    }
                                }.show()
                            }
                        }
                    }
                )
            )
        )
    }

    private fun showFavoriteToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showErrorMessageToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        viewModel.resetErrorMessage()
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