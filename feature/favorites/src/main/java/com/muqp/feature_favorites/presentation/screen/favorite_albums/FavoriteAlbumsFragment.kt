package com.muqp.feature_favorites.presentation.screen.favorite_albums

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
import com.muqp.beatbeat.favorites.databinding.FragmentFavoriteAlbumsBinding
import com.muqp.core_ui.adapters_for_recycler.AdapterFactory
import com.muqp.core_ui.bottom_sheet.DotsBottomSheetMenu
import com.muqp.core_ui.bottom_sheet.DotsBottomSheetMenu.showDotsMenu
import com.muqp.core_utils.extensions.ContextExt.openInBrowser
import com.muqp.core_utils.extensions.ContextExt.shareUrl
import com.muqp.core_utils.extensions.NavigationExt.navigate
import com.muqp.core_utils.extensions.NavigationExt.navigateBack
import com.muqp.core_utils.has_dependencies.HasDependencies
import com.muqp.feature_favorites.model.ItemAlbum
import kotlinx.coroutines.launch
import com.muqp.beatbeat.ui.R as CoreUi

class FavoriteAlbumsFragment : Fragment() {

    private var _binding: FragmentFavoriteAlbumsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: FavoriteAlbumsViewModel by viewModels { viewModelFactory }

    private val albumAdapter by lazy { AdapterFactory.createAdapter(R.layout.favorite_album_list_item) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteAlbumsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dependencies = (requireActivity() as HasDependencies).getViewModelFactory()
        viewModelFactory = dependencies.provideViewModelFactory()

        initListeners()
    }

    private fun initListeners() = with(binding) {
        favoriteAlbumsRv.layoutManager = LinearLayoutManager(context)
        favoriteAlbumsRv.adapter = albumAdapter

        viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getAllAlbums.collect { albums ->
                    if (albums.isNotEmpty()) {
                        favoriteAlbumsRv.visibility = View.VISIBLE
                        placeholderTv.visibility = View.GONE
                        val listAlbums = viewModel.loadFavoriteAlbumsDataRecycler(
                            listAlbums = albums,
                            onAlbumClick = { albumId ->
                                navigate(
                                    actionId = R.id.action_favoriteAlbumsFragment_to_albumDetailsFragment,
                                    intData = albumId
                                )
                            }
                        ) { view, item ->
                            showAlbumActionsMenu(view.context, item)
                        }
                        albumAdapter.submitList(listAlbums)
                    } else {
                        favoriteAlbumsRv.visibility = View.GONE
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

    private fun showAlbumActionsMenu(context: Context, album: ItemAlbum) {
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
                    iconRes = CoreUi.drawable.icons8_delete_v2,
                    title = getString(CoreUi.string.remove_from_favorites),
                    action = {
                        viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
                            viewModel.deleteAlbum(album)
                        }
                    }
                ),
                DotsBottomSheetMenu.BottomSheetAction(
                    iconRes = CoreUi.drawable.icons8_share_v2,
                    title = getString(CoreUi.string.to_share),
                    action = { context.shareUrl(album.shareUrl, album.name) }
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