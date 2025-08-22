package com.muqp.feature_favorites.presentation.screen.favorites

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.muqp.beatbeat.favorites.R
import com.muqp.beatbeat.favorites.databinding.FragmentFavoritesBinding
import com.muqp.core_ui.adapters_for_recycler.AdapterFactory
import com.muqp.core_ui.bottom_sheet.DotsBottomSheetMenu
import com.muqp.core_ui.bottom_sheet.DotsBottomSheetMenu.showDotsMenu
import com.muqp.core_ui.common.AnimationExpandedHelper
import com.muqp.core_ui.downloader.downloadMediaFile
import com.muqp.core_utils.extensions.ContextExt.openInBrowser
import com.muqp.core_utils.extensions.ContextExt.shareUrl
import com.muqp.core_utils.extensions.NavigationExt.navigate
import com.muqp.core_utils.extensions.NavigationExt.navigateBack
import com.muqp.core_utils.extensions.NavigationExt.navigationDataString
import com.muqp.core_utils.has_dependencies.HasDependencies
import com.muqp.feature_favorites.model.ItemTrack
import com.muqp.feature_favorites.presentation.common.CreatePlaylistDialog
import kotlinx.coroutines.launch
import com.muqp.beatbeat.ui.R as CoreUi

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: FavoritesViewModel by viewModels { viewModelFactory }

    private val playlistAdapter by lazy { AdapterFactory.createAdapter(R.layout.playlist_item) }
    private val playlistTracksAdapter by lazy { AdapterFactory.createAdapter(R.layout.favorite_track_list_item) }
    private var isPlaylistsExpanded = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dependencies = (requireActivity() as HasDependencies).getViewModelFactory()
        viewModelFactory = dependencies.provideViewModelFactory()

        initListeners()
    }

    private fun initListeners() = with(binding) {
        playlistsRv.layoutManager = LinearLayoutManager(context)
        playlistsRv.adapter = playlistAdapter
        playlistsRv.setHasFixedSize(true)

        val dialog = CreatePlaylistDialog(requireContext())
        createPlaylistFab.setIconResource(CoreUi.drawable.icons8_plus_v2)
        createPlaylistFab.setOnClickListener {
            dialog.onCreatePlaylist = { playlistName, playlistDescription ->
                viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
                    viewModel.createPlaylist(
                        name = playlistName,
                        description = playlistDescription
                    )
                }
            }
            dialog.show()
        }

        if (navigationDataString == CREATE_PLAYLIST) {
            dialog.apply {
                onCreatePlaylist = { playlistName, playlistDescription ->
                    viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
                        viewModel.createPlaylist(
                            name = playlistName,
                            description = playlistDescription
                        )
                    }
                }
                onDismiss = {
                    navigateBack()
                }

                show()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
            viewModel.errorMessage.collect { errorMessage ->
                errorMessage?.let {
                    showErrorMessageToast(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.playlists.collect { item ->
                    val playlists = viewModel.loadPlaylistDataRecycler(
                        playlists = item,
                        onDeletePlaylist = { playlistId ->
                            viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
                                viewModel.deletePlaylist(playlistId)
                            }
                        },
                        onPlaylistClicked = { playlistId ->
                            viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
                                viewModel.onPlaylistClicked(playlistId)
                            }
                        },
                        onBindTracks = { rv, playlistId ->
                            viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
                                rv.adapter = playlistTracksAdapter
                                if (playlistId != null) {
                                    viewModel.getPlaylistTracks(playlistId)
                                }
                                viewModel.tracks.collect { trackList ->
                                    val playlistTracks = viewModel.loadPlaylistTracksDataRecycler(
                                        tracks = trackList,
                                        onClickTrack = { track -> viewModel.onTrackClicked(track) }
                                    ) { view, item ->
                                        playlistId?.let {
                                            showTrackActionsMenu(view.context, item, playlistId)
                                        }
                                    }
                                    playlistTracksAdapter.submitList(playlistTracks)
                                }
                            }
                        }
                    )
                    playlistAdapter.submitList(playlists)
                }
            }
        }

        allFavoriteTracks.itemImage.setImageResource(CoreUi.drawable.icons8_track)
        allFavoriteAlbums.itemImage.setImageResource(CoreUi.drawable.icons8_album)
        allFavoriteArtists.itemImage.setImageResource(CoreUi.drawable.icons8_artist)
        allPlaylists.itemImage.setImageResource(CoreUi.drawable.icons8_playlist)

        allFavoriteTracks.itemTitle.text = getString(CoreUi.string.tracks)
        allFavoriteAlbums.itemTitle.text = getString(CoreUi.string.albums)
        allFavoriteArtists.itemTitle.text = getString(CoreUi.string.artists)
        allPlaylists.itemTitle.text = getString(CoreUi.string.playlists)

        allFavoriteTracks.root.setOnClickListener {
            navigate(
                actionId = R.id.action_favoritesFragment_to_favoriteTracksFragment
            )
        }

        allFavoriteAlbums.root.setOnClickListener {
            navigate(
                actionId = R.id.action_favoritesFragment_to_favoriteAlbumsFragment
            )
        }

        allFavoriteArtists.root.setOnClickListener {
            navigate(
                actionId = R.id.action_favoritesFragment_to_favoriteArtistsFragment
            )
        }

        allPlaylists.root.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
                if (isPlaylistsExpanded) {
                    viewModel.getPlaylists()
                }
            }
            toggleExpandedPlaylists()
        }
    }

    private fun toggleExpandedPlaylists() = with(binding) {
        if (playlistsRv.isVisible) {
            AnimationExpandedHelper.collapseViewWithAnimation(
                view = playlistsRv,
                arrowView = allPlaylists.itemUnwrap,
                animationResId = CoreUi.anim.expanded_slide_down
            )
        } else {
            AnimationExpandedHelper.expandViewWithAnimation(
                view = playlistsRv,
                arrowView = allPlaylists.itemUnwrap,
                animationResId = CoreUi.anim.expanded_slide_up
            )
        }
    }

    private fun showTrackActionsMenu(context: Context, track: ItemTrack, playlistId: Long) {
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
                    iconRes = CoreUi.drawable.icons8_delete,
                    title = getString(CoreUi.string.remove_from_playlist),
                    action = {
                        viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
                            viewModel.removeTrackFromPlaylist(playlistId, track.id)
                        }
                    }
                )
            )
        )
    }

    private fun showErrorMessageToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        viewModel.resetErrorMessage()
    }

    override fun onResume() {
        super.onResume()
        viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
            viewModel.getPlaylists()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val CREATE_PLAYLIST = "createPlaylist"
    }
}