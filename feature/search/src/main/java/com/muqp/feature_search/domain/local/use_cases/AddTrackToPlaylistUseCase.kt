package com.muqp.feature_search.domain.local.use_cases

import com.muqp.feature_search.domain.local.PlaylistRepository
import javax.inject.Inject

class AddTrackToPlaylistUseCase @Inject constructor(
    private val playlistRepository: PlaylistRepository
) {
    suspend operator fun invoke(playlistId: Long, trackId: String) {
        playlistRepository.addTrackToPlaylist(playlistId, trackId)
    }
}