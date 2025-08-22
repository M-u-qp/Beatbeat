package com.muqp.feature_listen.presentation.screen

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
import com.muqp.beatbeat.listen.R
import com.muqp.beatbeat.listen.databinding.FragmentListenBinding
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
import com.muqp.core_utils.has_dependencies.HasDependencies
import com.muqp.feature_listen.model.ItemTrackUI
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.muqp.beatbeat.ui.R as CoreUi

class ListenFragment : Fragment() {
    private var _binding: FragmentListenBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: ListenViewModel by viewModels { viewModelFactory }

    private lateinit var loadingView: LoadingView

    private val acousticTrackAdapter by lazy {
        AdapterFactory.createAdapter(
            R.layout.listen_track_list_item,
            5
        )
    }
    private val electrTrackAdapter by lazy {
        AdapterFactory.createAdapter(
            R.layout.listen_track_list_item,
            5
        )
    }
    private val femaleTrackAdapter by lazy {
        AdapterFactory.createAdapter(
            R.layout.listen_track_list_item,
            5
        )
    }
    private val maleTrackAdapter by lazy {
        AdapterFactory.createAdapter(
            R.layout.listen_track_list_item,
            5
        )
    }
    private val slowTrackAdapter by lazy {
        AdapterFactory.createAdapter(
            R.layout.listen_track_list_item,
            5
        )
    }
    private val fastTrackAdapter by lazy {
        AdapterFactory.createAdapter(
            R.layout.listen_track_list_item,
            5
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListenBinding.inflate(inflater, container, false)
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

    private fun initIncludes() = with(binding) {
        val sections = listOf(
            Triple(acousticInclude, acousticTrackAdapter, CoreUi.string.acoustic_collection),
            Triple(electrInclude, electrTrackAdapter, CoreUi.string.electric_collection),
            Triple(femaleInclude, femaleTrackAdapter, CoreUi.string.female_voices),
            Triple(maleInclude, maleTrackAdapter, CoreUi.string.male_voices),
            Triple(slowInclude, slowTrackAdapter, CoreUi.string.slow_collection),
            Triple(fastInclude, fastTrackAdapter, CoreUi.string.fast_collection)
        )

        sections.forEach { (layout, adapter, titleRes) ->
            layout.recyclerView.layoutManager = LinearLayoutManager(context)
            layout.recyclerView.adapter = adapter
            layout.titleCollectionTv.text = getString(titleRes)
        }
    }

    private fun initListeners() {
        initIncludes()

        viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
            viewModel.loadTrackCollections()

            viewModel.state.collectLatest { state ->
                updateUI(state)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
            viewModel.favoriteSideEffect.collect { result ->
                result?.let {
                    val message = when (result.favoriteResultType) {
                        ToggleFavorite.FavoriteResultType.ADDED -> {
                            getString(CoreUi.string.track_added_to_favorites)
                        }

                        ToggleFavorite.FavoriteResultType.REMOVED -> {
                            getString(CoreUi.string.track_removed_to_favorites)

                        }
                    }
                    showFavoriteToast(message)
                }
            }
        }
    }

    private fun updateUI(state: ListenScreenState?) {
        when (state) {
            is ListenScreenState.Loading -> {
                loadingView.show()
            }

            is ListenScreenState.Success -> {
                loadingView.hide()
                binding.serverProblemLayout.root.visibility = View.GONE
                binding.successLayout.visibility = View.VISIBLE

                if (state.acousticTrackData.results.isNotEmpty()) {
                    viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
                        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            val acousticTracks =
                                viewModel.loadTrackDataRecycler(
                                    track = state.acousticTrackData,
                                    onClickTrack = { track -> viewModel.onTrackClicked(track) }
                                ) { view, item ->
                                    showTrackActionsMenu(
                                        context = view.context,
                                        track = item
                                    )
                                }
                            acousticTrackAdapter.submitList(acousticTracks)
                            binding.acousticInclude.listenCollectionIv.setOnClickListener {
                                viewModel.onListenCollectionClicked(state.acousticTrackData.results)
                            }
                        }
                    }
                }

                if (state.electrTrackData.results.isNotEmpty()) {
                    viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
                        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            val electrTracks =
                                viewModel.loadTrackDataRecycler(
                                    track = state.electrTrackData,
                                    onClickTrack = { track -> viewModel.onTrackClicked(track) }
                                ) { view, item ->
                                    showTrackActionsMenu(
                                        context = view.context,
                                        track = item
                                    )
                                }
                            electrTrackAdapter.submitList(electrTracks)
                            binding.electrInclude.listenCollectionIv.setOnClickListener {
                                viewModel.onListenCollectionClicked(state.electrTrackData.results)
                            }
                        }
                    }
                }

                if (state.femaleTrackData.results.isNotEmpty()) {
                    viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
                        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            val femaleTracks =
                                viewModel.loadTrackDataRecycler(
                                    track = state.femaleTrackData,
                                    onClickTrack = { track -> viewModel.onTrackClicked(track) }
                                ) { view, item ->
                                    showTrackActionsMenu(
                                        context = view.context,
                                        track = item
                                    )
                                }
                            femaleTrackAdapter.submitList(femaleTracks)
                            binding.femaleInclude.listenCollectionIv.setOnClickListener {
                                viewModel.onListenCollectionClicked(state.femaleTrackData.results)
                            }
                        }
                    }
                }

                if (state.maleTrackData.results.isNotEmpty()) {
                    viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
                        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            val maleTracks =
                                viewModel.loadTrackDataRecycler(
                                    track = state.maleTrackData,
                                    onClickTrack = { track -> viewModel.onTrackClicked(track) }
                                ) { view, item ->
                                    showTrackActionsMenu(
                                        context = view.context,
                                        track = item
                                    )
                                }
                            maleTrackAdapter.submitList(maleTracks)
                            binding.maleInclude.listenCollectionIv.setOnClickListener {
                                viewModel.onListenCollectionClicked(state.maleTrackData.results)
                            }
                        }
                    }
                }

                if (state.slowTrackData.results.isNotEmpty()) {
                    viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
                        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            val slowTracks =
                                viewModel.loadTrackDataRecycler(
                                    track = state.slowTrackData,
                                    onClickTrack = { track -> viewModel.onTrackClicked(track) }
                                ) { view, item ->
                                    showTrackActionsMenu(
                                        context = view.context,
                                        track = item
                                    )
                                }
                            slowTrackAdapter.submitList(slowTracks)
                            binding.slowInclude.listenCollectionIv.setOnClickListener {
                                viewModel.onListenCollectionClicked(state.slowTrackData.results)
                            }
                        }
                    }
                }

                if (state.fastTrackData.results.isNotEmpty()) {
                    viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
                        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            val fastTracks =
                                viewModel.loadTrackDataRecycler(
                                    track = state.fastTrackData,
                                    onClickTrack = { track -> viewModel.onTrackClicked(track) }
                                ) { view, item ->
                                    showTrackActionsMenu(
                                        context = view.context,
                                        track = item
                                    )
                                }
                            fastTrackAdapter.submitList(fastTracks)
                            binding.fastInclude.listenCollectionIv.setOnClickListener {
                                viewModel.onListenCollectionClicked(state.fastTrackData.results)
                            }
                        }
                    }
                }
            }

            is ListenScreenState.Error -> {
                binding.serverProblemLayout.root.visibility = View.VISIBLE
                binding.successLayout.visibility = View.GONE
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
                                            actionId = R.id.action_listenFragment_to_favoritesFragment,
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
        private const val CREATE_PLAYLIST = "createPlaylist"
    }
}