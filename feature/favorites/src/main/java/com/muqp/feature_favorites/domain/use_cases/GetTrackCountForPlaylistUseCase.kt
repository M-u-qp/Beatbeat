package com.muqp.feature_favorites.domain.use_cases

import com.muqp.feature_favorites.domain.PlaylistRepository
import javax.inject.Inject

class GetTrackCountForPlaylistUseCase @Inject constructor(
    private val playlistRepository: PlaylistRepository
) {
    suspend operator fun invoke(playlistId: Long): Int {
        return playlistRepository.getTrackCountForPlaylist(playlistId)
    }
}