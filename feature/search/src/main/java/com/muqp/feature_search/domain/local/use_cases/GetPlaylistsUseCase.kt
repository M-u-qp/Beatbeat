package com.muqp.feature_search.domain.local.use_cases

import com.muqp.feature_search.domain.local.PlaylistRepository
import com.muqp.feature_search.model.PlaylistUI
import javax.inject.Inject

class GetPlaylistsUseCase @Inject constructor(
    private val playlistRepository: PlaylistRepository
) {
    suspend operator fun invoke(): List<PlaylistUI> {
        return playlistRepository.getAllPlaylists()
    }
}