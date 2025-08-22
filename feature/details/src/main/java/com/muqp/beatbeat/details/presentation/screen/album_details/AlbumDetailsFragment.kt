package com.muqp.beatbeat.details.presentation.screen.album_details

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
import com.muqp.beatbeat.details.R
import com.muqp.beatbeat.details.databinding.FragmentAlbumDetailsBinding
import com.muqp.beatbeat.details.model.ItemAlbumUI
import com.muqp.beatbeat.details.model.ItemTrackUI
import com.muqp.beatbeat.details.presentation.screen.artist_details.ArtistDetailsFragment.Companion.CREATE_PLAYLIST
import com.muqp.core_ui.adapters_for_recycler.AdapterFactory
import com.muqp.core_ui.bottom_sheet.DotsBottomSheetMenu
import com.muqp.core_ui.bottom_sheet.DotsBottomSheetMenu.showDotsMenu
import com.muqp.core_ui.common.ToggleFavorite
import com.muqp.core_ui.custom_view.LoadingView
import com.muqp.core_ui.dialog.PlaylistSelectionDialog
import com.muqp.core_ui.downloader.downloadMediaFile
import com.muqp.core_utils.extensions.NavigationExt.navigate
import com.muqp.core_utils.extensions.NavigationExt.navigateBack
import com.muqp.core_utils.extensions.NavigationExt.navigationDataInt
import com.muqp.core_utils.has_dependencies.HasDependencies
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.muqp.beatbeat.ui.R as CoreUi

class AlbumDetailsFragment : Fragment() {
    private var _binding: FragmentAlbumDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: AlbumDetailViewModel by viewModels { viewModelFactory }

    private lateinit var loadingView: LoadingView

    private val trackAdapter by lazy { AdapterFactory.createAdapter(R.layout.album_tracks_list_item) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlbumDetailsBinding.inflate(inflater, container, false)
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
        viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
            albumTracksRv.layoutManager = LinearLayoutManager(context)
            albumTracksRv.adapter = trackAdapter

            navigationDataInt?.let {
                viewModel.loadAlbumById(it)
            }

            viewModel.state.collectLatest { state ->
                updateUI(state)
            }
        }

        backIconIv.setOnClickListener {
            navigateBack()
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

    }

    private fun updateUI(state: AlbumDetailsScreenState?) {
        when (state) {
            is AlbumDetailsScreenState.Loading -> {
                loadingView.show()
            }

            is AlbumDetailsScreenState.Success -> {
                binding.successLayout.visibility = View.VISIBLE
                binding.serverProblemLayout.root.visibility = View.GONE
                loadingView.hide()
                if (state.albumData?.tracks?.isNotEmpty() == true) {
                    viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
                        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            val tracksList =
                                viewModel.loadAlbumTracksDataRecycler(
                                    item = state.albumData,
                                    onClickTrack = { track -> viewModel.onTrackClicked(track) }
                                ) { view, item ->
                                    showTrackActionsMenu(
                                        context = view.context,
                                        track = item
                                    )
                                }
                            trackAdapter.submitList(tracksList)
                        }
                    }
                }

                state.albumData?.let { data ->
                    updateAlbumDetails(
                        albumName = data.name,
                        artistName = data.artistName,
                        releaseDate = data.releaseDate,
                        image = data.image
                    )

                    binding.dotsIconIv.setOnClickListener {
                        showAlbumActionsMenu(requireContext(), data)
                    }

                    viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
                        val isFavoriteInitial = viewModel.getFavoriteInitial(data)
                        ToggleFavorite.updateColorFavoriteIcon(
                            binding.favoriteIconIv,
                            isFavoriteInitial,
                            trueColor = CoreUi.color.genre_pop,
                            falseColor = CoreUi.color.black
                        )
                    }

                    binding.favoriteIconIv.setOnClickListener {
                        viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
                            viewModel.toggleAlbumFavorite(data) { newFavoriteState ->
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
            }

            is AlbumDetailsScreenState.Error -> {
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

    private fun updateAlbumDetails(
        albumName: String,
        artistName: String,
        releaseDate: String,
        image: String
    ) = with(binding) {
        albumNameTv.text = albumName
        artistNameTv.text = artistName
        albumDateReleaseTv.text = releaseDate
        Glide.with(requireContext())
            .load(image)
            .placeholder(CoreUi.drawable.icons8_beatbeat)
            .error(CoreUi.drawable.icons8_broken_image)
            .into(albumImageIv)
    }

    private fun showAlbumActionsMenu(
        context: Context,
        album: ItemAlbumUI
    ) {
        context.showDotsMenu(
            title = album.name,
            imageUrl = album.image,
            actions = listOf()
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
                                            actionId = R.id.action_albumDetailsFragment_to_favoritesFragment,
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
}