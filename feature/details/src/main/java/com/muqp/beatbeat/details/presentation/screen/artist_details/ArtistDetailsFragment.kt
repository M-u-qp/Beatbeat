package com.muqp.beatbeat.details.presentation.screen.artist_details

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
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.muqp.beatbeat.details.R
import com.muqp.beatbeat.details.databinding.FragmentArtistDetailsBinding
import com.muqp.beatbeat.details.model.ItemArtistUI
import com.muqp.beatbeat.details.model.ItemTrackUI
import com.muqp.core_ui.adapters_for_recycler.AdapterFactory
import com.muqp.core_ui.bottom_sheet.DotsBottomSheetMenu
import com.muqp.core_ui.bottom_sheet.DotsBottomSheetMenu.showDotsMenu
import com.muqp.core_ui.common.ToggleFavorite
import com.muqp.core_ui.custom_view.LoadingView
import com.muqp.core_ui.dialog.PlaylistSelectionDialog
import com.muqp.core_ui.downloader.downloadMediaFile
import com.muqp.core_utils.extensions.ContextExt.openInBrowser
import com.muqp.core_utils.extensions.ContextExt.shareUrl
import com.muqp.core_utils.extensions.NavigationExt.navigate
import com.muqp.core_utils.extensions.NavigationExt.navigateBack
import com.muqp.core_utils.extensions.NavigationExt.navigationDataString
import com.muqp.core_utils.has_dependencies.HasDependencies
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.muqp.beatbeat.ui.R as CoreUi

class ArtistDetailsFragment : Fragment() {
    private var _binding: FragmentArtistDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: ArtistDetailViewModel by viewModels { viewModelFactory }

    private lateinit var loadingView: LoadingView

    private val albumsAdapter by lazy { AdapterFactory.createAdapter(R.layout.artist_albums_item_list) }
    private val tracksAdapter by lazy {
        AdapterFactory.createAdapter(
            layoutResId = R.layout.album_tracks_list_item,
            itemCount = 5
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArtistDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingView = LoadingView(requireContext())
        (binding.root.getChildAt(0) as ViewGroup).addView(loadingView)

        val dependencies = (requireActivity() as HasDependencies).getViewModelFactory()
        viewModelFactory = dependencies.provideViewModelFactory()

        initListeners()
    }

    private fun initListeners() = with(binding) {
        viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
            albumsRv.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            albumsRv.adapter = albumsAdapter

            topTracksRv.layoutManager = LinearLayoutManager(context)
            topTracksRv.adapter = tracksAdapter

            navigationDataString?.let {
                val artist = Gson().fromJson(it, ItemArtistUI::class.java)
                viewModel.getArtistDetailsData(artist.id.toInt())

                Glide.with(requireContext())
                    .load(artist.image)
                    .placeholder(CoreUi.drawable.icons8_beatbeat)
                    .error(CoreUi.drawable.icons8_broken_image)
                    .into(binding.artistImageIv)

                binding.artistNameTv.text = artist.name

                binding.dotsIconIv.setOnClickListener {
                    showArtistActionsMenu(requireContext(), artist)
                }

                allPopularTracksIv.setOnClickListener {
                    navigate(
                        actionId = R.id.action_artistDetailsFragment_to_allTracksFragment,
                        intData = artist.id.toInt()
                    )
                }

                val isFavoriteInitial = viewModel.getFavoriteInitial(artist)
                ToggleFavorite.updateColorFavoriteIcon(
                    binding.favoriteIconIv,
                    isFavoriteInitial,
                    trueColor = CoreUi.color.genre_pop,
                    falseColor = CoreUi.color.black
                )

                binding.favoriteIconIv.setOnClickListener {
                    viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
                        viewModel.toggleArtistFavorite(artist) { newFavoriteState ->
                            ToggleFavorite.updateColorFavoriteIcon(
                                binding.favoriteIconIv,
                                newFavoriteState,
                                trueColor = CoreUi.color.genre_pop,
                                falseColor = CoreUi.color.black
                            )
                        }
                    }
                }
            }

            viewModel.state.collectLatest { state ->
                updateUI(state)
            }
        }

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
                }
            }
        }

        backIconIv.setOnClickListener {
            navigateBack()
        }
    }

    private fun updateUI(state: ArtistDetailsScreenState?) {
        when (state) {
            is ArtistDetailsScreenState.Loading -> {
                loadingView.show()
            }

            is ArtistDetailsScreenState.Success -> {
                binding.successLayout.visibility = View.VISIBLE
                binding.serverProblemLayout.root.visibility = View.GONE
                loadingView.hide()
                if (state.albums?.results?.isNotEmpty() == true) {
                    viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
                        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            val albums =
                                viewModel.loadArtistAlbumsDataRecycler(state.albums) { albumId ->
                                    navigate(
                                        actionId = R.id.action_artistDetailsFragment_to_albumDetailsFragment,
                                        intData = albumId
                                    )
                                }
                            albumsAdapter.submitList(albums)
                        }
                    }
                }

                if (state.popularTracks.isNotEmpty()) {
                    viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
                        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            val tracks =
                                viewModel.loadPopularArtistTracksDataRecycler(
                                    tracks = state.popularTracks,
                                    onClickTrack = { track -> viewModel.onTrackClicked(track) }
                                ) { view, item ->
                                    showTrackActionsMenu(view.context, item)
                                }
                            tracksAdapter.submitList(tracks)
                        }
                    }
                }
            }

            is ArtistDetailsScreenState.Error -> {
                binding.successLayout.visibility = View.GONE
                binding.serverProblemLayout.root.visibility = View.VISIBLE
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

    private fun showArtistActionsMenu(
        context: Context,
        artist: ItemArtistUI
    ) {
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
                                            actionId = R.id.action_artistDetailsFragment_to_favoritesFragment,
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

    override fun onStop() {
        super.onStop()
        viewModel.resetFavoriteSideEffect()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.root.removeView(loadingView)
        _binding = null
    }

    companion object {
        const val CREATE_PLAYLIST = "createPlaylist"
    }
}