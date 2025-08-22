package com.muqp.beatbeat.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.util.UnstableApi
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.muqp.beatbeat.MusicApp
import com.muqp.beatbeat.R
import com.muqp.beatbeat.databinding.FragmentMainBinding
import com.muqp.beatbeat.exo_player.PlayerManager
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.muqp.beatbeat.ui.R as CoreUi

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var playerManager: PlayerManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MusicApp.musicAppComponent.inject(this)

        val navView: BottomNavigationView = binding.navView
        val navHostFragment =
            childFragmentManager.findFragmentById(CoreUi.id.host_main) as NavHostFragment
        val navController = navHostFragment.findNavController()

        navController.addOnDestinationChangedListener { _, destination, _ ->
            navView.visibility = when (destination.id) {
                R.id.homeFragment, R.id.searchFragment,
                R.id.listenFragment, R.id.favoritesFragment -> View.VISIBLE

                else -> View.GONE
            }
            playerListeners(destinationId = destination.id)
        }

        navView.setupWithNavController(navController)
    }


    @OptIn(UnstableApi::class)
    private fun playerListeners(destinationId: Int) = with(binding) {
        playerView.initPlayerView(
            playerManager = playerManager,
            lifecycleOwner = viewLifecycleOwner
        )

        playerView.onFullscreenChanged = { isFullscreen ->
            navView.visibility = when {
                isFullscreen -> {
                    View.GONE
                }
                else -> {
                    if (destinationId in listOf(
                            R.id.homeFragment, R.id.searchFragment,
                            R.id.favoritesFragment, R.id.listenFragment
                        )
                    ) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewLifecycleOwner.lifecycleScope.launch {
            playerManager.checkServiceState()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}