package com.muqp.feature_search.presentation.screen

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.gson.Gson
import com.muqp.beatbeat.search.R
import com.muqp.beatbeat.search.databinding.FragmentSearchBinding
import com.muqp.core_ui.adapters_for_recycler.AdapterFactory
import com.muqp.core_ui.bottom_sheet.DotsBottomSheetMenu
import com.muqp.core_ui.bottom_sheet.DotsBottomSheetMenu.showDotsMenu
import com.muqp.core_ui.common.CustomSpanHintSearchView
import com.muqp.core_ui.common.HideKeyboard
import com.muqp.core_ui.common.ToggleFavorite
import com.muqp.core_ui.custom_view.LoadingView
import com.muqp.core_ui.dialog.PlaylistSelectionDialog
import com.muqp.core_ui.downloader.downloadMediaFile
import com.muqp.core_utils.extensions.ContextExt.openInBrowser
import com.muqp.core_utils.extensions.ContextExt.shareUrl
import com.muqp.core_utils.extensions.NavigationExt.navigate
import com.muqp.core_utils.has_dependencies.HasDependencies
import com.muqp.feature_search.common.SearchQueryType
import com.muqp.feature_search.model.ItemAlbumUI
import com.muqp.feature_search.model.ItemArtistUI
import com.muqp.feature_search.model.ItemTrackUI
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.muqp.beatbeat.ui.R as CoreUi

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: SearchViewModel by viewModels { viewModelFactory }

    private var queryTextJob: Job? = null
    private var favoriteToastJob: Job? = null

    private lateinit var loadingView: LoadingView

    private val artistAdapter by lazy { AdapterFactory.createAdapter(R.layout.artist_list_item, 5) }
    private val albumAdapter by lazy { AdapterFactory.createAdapter(R.layout.album_list_item, 5) }
    private val trackAdapter by lazy { AdapterFactory.createAdapter(R.layout.track_list_item, 5) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchResultCustomView.initAdapters(
            artistAdapter = artistAdapter,
            albumAdapter = albumAdapter,
            trackAdapter = trackAdapter
        )

        loadingView = LoadingView(requireContext())
        binding.root.addView(loadingView)

        val dependencies = (requireActivity() as HasDependencies).getViewModelFactory()
        viewModelFactory = dependencies.provideViewModelFactory()

        CustomSpanHintSearchView.getTypefaceSearchView(
            hintTextId = CoreUi.string.search_hint,
            fontId = CoreUi.font.shantell_sans_regular,
            context = requireContext(),
            searchView = binding.searchBarSv
        )

        initListeners()
    }

    private fun initListeners() = with(binding) {
        searchBarSv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                HideKeyboard.hideKeyboard(searchBarSv)
                return true
            }

            override fun onQueryTextChange(inputText: String?): Boolean {
                queryTextJob?.cancel()
                inputText?.let { text ->
                    if (text.length >= 3 && viewModel.currentSearchText != text) {
                        queryTextJob =
                            viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
                                delay(2000)
                                HideKeyboard.hideKeyboard(searchBarSv)
                                viewModel.loadSearchResult(inputText)
                            }
                    }
                }
                return true
            }
        })

        viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
            viewModel.state.collect { state ->
                updateUI(state)
            }
        }

        genreSearchView.setOnGenreClickListener { genre ->
            navigate(
                actionId = R.id.action_searchFragment_to_genreSearchFragment,
                stringData = genre
            )
        }
    }

    private fun updateUI(state: SearchScreenState?) = with(binding) {
        when (state) {
            is SearchScreenState.NoInputData -> {
                loadingView.hide()
                searchNotFoundLl.visibility = View.GONE
                genreSearchView.show()
            }

            is SearchScreenState.Loading -> {
                loadingView.show()
                scrollableGenresFilter.visibility = View.GONE
                genreSearchView.hide()
                scrollableSearchResult.visibility = View.VISIBLE
                searchResultCustomView.show()
                searchNotFoundLl.visibility = View.GONE
            }

            is SearchScreenState.NotFound -> {
                searchResultCustomView.hide()
                scrollableSearchResult.visibility = View.GONE
                loadingView.hide()
                searchNotFoundLl.visibility = View.VISIBLE
            }

            is SearchScreenState.Success -> {
                loadingView.hide()

                if (state.trackData.results.isNotEmpty()) {
                    searchResultCustomView.isEmptyRecycler(false, SearchQueryType.TRACKS)
                    searchResultCustomView.setFoundDataInAdapter(SearchQueryType.TRACKS) { adapter ->
                        viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
                            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                                val trackDataList =
                                    viewModel.loadTrackDataRecycler(
                                        track = state.trackData,
                                        onClickTrack = { track -> viewModel.onTrackClicked(track) }
                                    ) { view, item ->
                                        showTrackActionsMenu(view.context, item)
                                    }
                                adapter.submitList(trackDataList)
                            }
                        }
                    }
                } else {
                    searchResultCustomView.isEmptyRecycler(true, SearchQueryType.TRACKS)
                }

                if (state.albumData.results.isNotEmpty()) {
                    searchResultCustomView.isEmptyRecycler(false, SearchQueryType.ALBUMS)
                    searchResultCustomView.setFoundDataInAdapter(SearchQueryType.ALBUMS) { adapter ->
                        viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
                            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                                val albumDataList = viewModel.loadAlbumDataRecycler(
                                    data = state.albumData,
                                    onAlbumClick = { albumId ->
                                        navigate(
                                            R.id.action_searchFragment_to_albumDetailsFragment,
                                            intData = albumId
                                        )
                                    },
                                    onMenuClickListener = { view, item ->
                                        showAlbumActionsMenu(view.context, item)
                                    }
                                )
                                adapter.submitList(albumDataList)
                            }
                        }
                    }
                } else {
                    searchResultCustomView.isEmptyRecycler(true, SearchQueryType.ALBUMS)
                }

                if (state.artistData.results.isNotEmpty()) {
                    searchResultCustomView.isEmptyRecycler(false, SearchQueryType.ARTISTS)
                    searchResultCustomView.setFoundDataInAdapter(SearchQueryType.ARTISTS) { adapter ->
                        viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
                            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                                val artistDataList = viewModel.loadArtistDataRecycler(
                                    data = state.artistData,
                                    onArtistClick = { artist ->
                                        navigate(
                                            R.id.action_searchFragment_to_artistDetailsFragment,
                                            stringData = Gson().toJson(artist)
                                        )
                                    },
                                    onMenuClickListener = { view, item ->
                                        showArtistActionsMenu(view.context, item)
                                    }
                                )
                                adapter.submitList(artistDataList)
                            }
                        }
                    }
                } else {
                    searchResultCustomView.isEmptyRecycler(true, SearchQueryType.ARTISTS)
                }

                favoriteToastJob =
                    viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
                        viewModel.favoriteSideEffect.collect { result ->
                            result?.let {
                                val message = when (result.favoriteType) {
                                    ToggleFavorite.FavoriteType.TRACK -> {
                                        when (result.favoriteResultType) {
                                            ToggleFavorite.FavoriteResultType.ADDED -> getString(
                                                CoreUi.string.track_added_to_favorites
                                            )

                                            ToggleFavorite.FavoriteResultType.REMOVED -> getString(
                                                CoreUi.string.track_removed_to_favorites
                                            )
                                        }
                                    }

                                    ToggleFavorite.FavoriteType.ALBUM -> {
                                        when (result.favoriteResultType) {
                                            ToggleFavorite.FavoriteResultType.ADDED -> getString(
                                                CoreUi.string.album_added_to_favorites
                                            )

                                            ToggleFavorite.FavoriteResultType.REMOVED -> getString(
                                                CoreUi.string.album_removed_to_favorites
                                            )
                                        }
                                    }

                                    ToggleFavorite.FavoriteType.ARTIST -> {
                                        when (result.favoriteResultType) {
                                            ToggleFavorite.FavoriteResultType.ADDED -> getString(
                                                CoreUi.string.artist_added_to_favorites
                                            )

                                            ToggleFavorite.FavoriteResultType.REMOVED -> getString(
                                                CoreUi.string.artist_removed_to_favorites
                                            )
                                        }
                                    }
                                }
                                showFavoriteToast(message)
                                viewModel.resetFavoriteSideEffect()
                            }
                        }
                    }

                searchResultCustomView.onClickAllFoundResults { positionTab ->
                    navigate(
                        actionId = R.id.action_searchFragment_to_foundResultMainFragment,
                        intData = positionTab,
                        stringData = viewModel.currentSearchText
                    )
                }
            }

            is SearchScreenState.Error -> {
                loadingView.hide()
                Toast.makeText(
                    requireContext(),
                    state.message,
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {}
        }
    }

    private fun showTrackActionsMenu(context: Context, track: ItemTrackUI) {
        context.showDotsMenu(
            title = track.name,
            imageUrl = track.image,
            name = track.artistName,
            actions = listOf(
                DotsBottomSheetMenu.BottomSheetAction(
                    iconRes = CoreUi.drawable.icons8_browsable,
                    title = getString(CoreUi.string.browsable),
                    action = { context.openInBrowser(track.shareUrl) }
                ),
                DotsBottomSheetMenu.BottomSheetAction(
                    iconRes = CoreUi.drawable.icons8_share_v2,
                    title = getString(CoreUi.string.to_share),
                    action = { context.shareUrl(track.shareUrl, track.name) }
                ),
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
                                            actionId = R.id.action_searchFragment_to_favoritesFragment,
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

    private fun showArtistActionsMenu(context: Context, artist: ItemArtistUI) {
        context.showDotsMenu(
            title = artist.name,
            imageUrl = artist.image,
            actions = listOf(
                DotsBottomSheetMenu.BottomSheetAction(
                    iconRes = CoreUi.drawable.icons8_browsable,
                    title = getString(CoreUi.string.browsable),
                    action = { context.openInBrowser(artist.shareUrl) }
                ),
                DotsBottomSheetMenu.BottomSheetAction(
                    iconRes = CoreUi.drawable.icons8_share_v2,
                    title = getString(CoreUi.string.to_share),
                    action = { context.shareUrl(artist.shareUrl, artist.name) }
                )
            )
        )
    }

    private fun showAlbumActionsMenu(context: Context, album: ItemAlbumUI) {
        context.showDotsMenu(
            title = album.name,
            imageUrl = album.image,
            name = album.artistName,
            actions = listOf(
                DotsBottomSheetMenu.BottomSheetAction(
                    iconRes = CoreUi.drawable.icons8_browsable,
                    title = getString(CoreUi.string.browsable),
                    action = { context.openInBrowser(album.shareUrl) }
                ),
                DotsBottomSheetMenu.BottomSheetAction(
                    iconRes = CoreUi.drawable.icons8_share_v2,
                    title = getString(CoreUi.string.to_share),
                    action = { context.shareUrl(album.shareUrl, album.name) }
                )
            )
        )
    }

    private fun showFavoriteToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.currentSearchText.isNotEmpty()) {
            viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
                viewModel.loadSearchResult(viewModel.currentSearchText)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        favoriteToastJob?.cancel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        queryTextJob?.cancel()
        favoriteToastJob?.cancel()
        binding.root.removeView(loadingView)
        _binding = null
    }

    companion object {
        const val CREATE_PLAYLIST = "createPlaylist"
    }
}