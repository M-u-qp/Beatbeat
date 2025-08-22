package com.muqp.beatbeat.details.domain.local.use_cases

import com.muqp.beatbeat.details.domain.local.PlaylistRepository
import javax.inject.Inject

class AddTrackToPlaylistUseCase @Inject constructor(
    private val playlistRepository: PlaylistRepository
) {
    suspend operator fun invoke(playlistId: Long, trackId: String) {
        playlistRepository.addTrackToPlaylist(playlistId, trackId)
    }
}