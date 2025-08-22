package com.muqp.feature_favorites.presentation.screen.favorite_artists

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
import com.muqp.beatbeat.favorites.R
import com.muqp.beatbeat.favorites.databinding.FragmentFavoriteArtistsBinding
import com.muqp.core_ui.adapters_for_recycler.AdapterFactory
import com.muqp.core_ui.bottom_sheet.DotsBottomSheetMenu
import com.muqp.core_ui.bottom_sheet.DotsBottomSheetMenu.showDotsMenu
import com.muqp.core_utils.extensions.ContextExt.openInBrowser
import com.muqp.core_utils.extensions.ContextExt.shareUrl
import com.muqp.core_utils.extensions.NavigationExt.navigate
import com.muqp.core_utils.extensions.NavigationExt.navigateBack
import com.muqp.core_utils.has_dependencies.HasDependencies
import com.muqp.feature_favorites.model.ItemArtist
import com.muqp.beatbeat.ui.R as CoreUi
import kotlinx.coroutines.launch

class FavoriteArtistsFragment : Fragment() {

    private var _binding: FragmentFavoriteArtistsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: FavoriteArtistsViewModel by viewModels { viewModelFactory }

    private val artistAdapter by lazy { AdapterFactory.createAdapter(R.layout.favorite_artist_list_item) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteArtistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dependencies = (requireActivity() as HasDependencies).getViewModelFactory()
        viewModelFactory = dependencies.provideViewModelFactory()

        initListeners()
    }

    private fun initListeners() = with(binding) {
        favoriteArtistsRv.layoutManager = LinearLayoutManager(context)
        favoriteArtistsRv.adapter = artistAdapter

        viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getAllArtists.collect { artists ->
                    if (artists.isNotEmpty()) {
                        favoriteArtistsRv.visibility = View.VISIBLE
                        placeholderTv.visibility = View.GONE
                        val listArtists = viewModel.loadFavoriteArtistsDataRecycler(
                            listArtists = artists,
                            onArtistClick = { artist ->
                                navigate(
                                    actionId = R.id.action_favoriteArtistsFragment_to_artistDetailsFragment,
                                    stringData = Gson().toJson(artist)
                                )
                            }
                        ) { view, item ->
                            showArtistActionsMenu(view.context, item)
                        }
                        artistAdapter.submitList(listArtists)
                    } else {
                        favoriteArtistsRv.visibility = View.GONE
                        placeholderTv.visibility = View.VISIBLE
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

        backIconIv.setOnClickListener {
            navigateBack()
        }
    }

    private fun showArtistActionsMenu(context: Context, artist: ItemArtist) {
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
                    iconRes = CoreUi.drawable.icons8_delete_v2,
                    title = getString(CoreUi.string.remove_from_favorites),
                    action = {
                        viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
                            viewModel.deleteArtist(artist)
                        }
                    }
                ),
                DotsBottomSheetMenu.BottomSheetAction(
                    iconRes = CoreUi.drawable.icons8_share_v2,
                    title = getString(CoreUi.string.to_share),
                    action = { context.shareUrl(artist.shareUrl, artist.name) }
                )
            )
        )
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