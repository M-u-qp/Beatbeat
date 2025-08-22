package com.muqp.feature_favorites.presentation.screen.favorite_tracks

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
import com.muqp.beatbeat.favorites.R
import com.muqp.beatbeat.favorites.databinding.FragmentFavoriteTracksBinding
import com.muqp.core_ui.adapters_for_recycler.AdapterFactory
import com.muqp.core_ui.bottom_sheet.DotsBottomSheetMenu
import com.muqp.core_ui.bottom_sheet.DotsBottomSheetMenu.showDotsMenu
import com.muqp.core_ui.dialog.PlaylistSelectionDialog
import com.muqp.core_ui.downloader.downloadMediaFile
import com.muqp.core_utils.extensions.ContextExt.openInBrowser
import com.muqp.core_utils.extensions.ContextExt.shareUrl
import com.muqp.core_utils.extensions.NavigationExt.navigateBack
import com.muqp.core_utils.has_dependencies.HasDependencies
import com.muqp.feature_favorites.model.ItemTrack
import kotlinx.coroutines.launch
import com.muqp.beatbeat.ui.R as CoreUi

class FavoriteTracksFragment : Fragment() {

    private var _binding: FragmentFavoriteTracksBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: FavoriteTracksViewModel by viewModels { viewModelFactory }

    private val trackAdapter by lazy { AdapterFactory.createAdapter(R.layout.favorite_track_list_item) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dependencies = (requireActivity() as HasDependencies).getViewModelFactory()
        viewModelFactory = dependencies.provideViewModelFactory()

        initListeners()
    }

    private fun initListeners() = with(binding) {
        favoriteTracksRv.layoutManager = LinearLayoutManager(context)
        favoriteTracksRv.adapter = trackAdapter

        viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
            viewModel.errorMessage.collect { errorMessage ->
                errorMessage?.let {
                    showErrorMessageToast(errorMessage)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getAllTracks.collect { tracks ->
                    if (tracks.isNotEmpty()) {
                        favoriteTracksRv.visibility = View.VISIBLE
                        placeholderTv.visibility = View.GONE
                        val listTracks = viewModel.loadFavoriteTracksDataRecycler(
                            listTracks = tracks,
                            onClickTrack = { track -> viewModel.onTrackClicked(track) }
                        ) { view, item ->
                            showTrackActionsMenu(view.context, item)
                        }
                        trackAdapter.submitList(listTracks)
                    } else {
                        favoriteTracksRv.visibility = View.GONE
                        placeholderTv.visibility = View.VISIBLE
                    }
                }
            }
        }

        backIconIv.setOnClickListener {
            navigateBack()
        }
    }

    private fun showTrackActionsMenu(context: Context, track: ItemTrack) {
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
                    iconRes = CoreUi.drawable.icons8_delete_v2,
                    title = getString(CoreUi.string.remove_from_favorites),
                    action = {
                        viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
                            viewModel.deleteTrack(track)
                        }
                    }
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
                                val dialogItems = playlists.map { playlist ->
                                    PlaylistSelectionDialog.PlaylistSelectionItem(
                                        id = playlist.id ?: 0L,
                                        name = playlist.name,
                                        description = playlist.description
                                    )
                                }
                                PlaylistSelectionDialog(
                                    context = requireContext(),
                                    playlists = dialogItems
                                ).apply {
                                    onPlaylistSelected = { selectedItem ->
                                        viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
                                            viewModel.addTrackToPlaylist(selectedItem.id, track.id)

                                            showFavoriteToast(
                                                "${track.name} ${getString(CoreUi.string.added_to)} ${selectedItem.name}"
                                            )
                                        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}