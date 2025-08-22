package com.muqp.feature_home.presentation.screen

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
import com.muqp.beatbeat.home.R
import com.muqp.beatbeat.home.databinding.FragmentHomeBinding
import com.muqp.core_ui.adapters_for_recycler.AdapterFactory
import com.muqp.core_ui.bottom_sheet.DotsBottomSheetMenu
import com.muqp.core_ui.bottom_sheet.DotsBottomSheetMenu.showDotsMenu
import com.muqp.core_ui.custom_view.LoadingView
import com.muqp.core_utils.extensions.ContextExt.openInBrowser
import com.muqp.core_utils.extensions.ContextExt.shareUrl
import com.muqp.core_utils.extensions.NavigationExt.navigate
import com.muqp.core_utils.has_dependencies.HasDependencies
import com.muqp.feature_home.model.FeedItemUI
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.muqp.beatbeat.ui.R as CoreUi

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: HomeViewModel by viewModels { viewModelFactory }
    private val feedsAdapter by lazy { AdapterFactory.createPagingAdapter(R.layout.feed_list_item) }
    private lateinit var loadingView: LoadingView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
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
        feedRv.layoutManager = LinearLayoutManager(requireContext())
        feedRv.adapter = feedsAdapter

        viewModel.loadMusicFeeds()

        settingsIv.setOnClickListener {
            showSettingActionsMenu(
                requireContext(),
                onSignOut = {
                    viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
                        viewModel.signOut()
                    }
                }
            )
        }

        viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
            viewModel.state.collectLatest { state ->
                when (state) {
                    is HomeScreenState.SignOut -> {
                        navigate(
                            actionId = R.id.action_homeFragment_to_authFragment,
                            hostId = CoreUi.id.host_main
                        )
                        requireActivity().finish()
                    }

                    is HomeScreenState.Success -> {
                        successLayout.visibility = View.VISIBLE
                        serverProblemLayout.root.visibility = View.GONE
                        viewLifecycleOwner.lifecycleScope.launch(viewModel.coroutineEH) {
                            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                                val feedsList =
                                    viewModel.loadFeedsDataRecycler(state.pagingData) { view, item ->
                                        showFeedActionsMenu(view.context, item)
                                    }
                                feedsAdapter.submitData(feedsList)
                            }
                        }

                    }

                    is HomeScreenState.Error -> {
                        successLayout.visibility = View.GONE
                        serverProblemLayout.root.visibility = View.VISIBLE
                        serverProblemLayout.updateStateBtn.setOnClickListener {
                            viewModel.loadMusicFeeds()
                        }
                        Toast.makeText(
                            requireContext(),
                            CoreUi.string.unknown_error,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {}
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
            feedsAdapter.loadStateFlow.collectLatest { loadState ->
                viewModel.handlePaginationLoadState(loadState)
            }
        }
    }

    private fun showFeedActionsMenu(context: Context, feed: FeedItemUI) {
        context.showDotsMenu(
            title = feed.title.en,
            imageUrl = feed.images.size315X111,
            actions = listOf(
                DotsBottomSheetMenu.BottomSheetAction(
                    iconRes = CoreUi.drawable.icons8_browsable,
                    title = getString(CoreUi.string.browsable),
                    action = { context.openInBrowser(feed.link) }
                ),
                DotsBottomSheetMenu.BottomSheetAction(
                    iconRes = CoreUi.drawable.icons8_share_v2,
                    title = getString(CoreUi.string.to_share),
                    action = { context.shareUrl(feed.link, feed.title.en) }
                )
            )
        )
    }

    private fun showSettingActionsMenu(
        context: Context,
        onSignOut: () -> Unit
    ) {
        context.showDotsMenu(
            title = getString(CoreUi.string.settings),
            actions = listOf(
                DotsBottomSheetMenu.BottomSheetAction(
                    iconRes = CoreUi.drawable.icons8_exit,
                    title = getString(CoreUi.string.log_out),
                    action = { onSignOut.invoke() }
                )
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.root.removeView(loadingView)
        _binding = null
    }
}