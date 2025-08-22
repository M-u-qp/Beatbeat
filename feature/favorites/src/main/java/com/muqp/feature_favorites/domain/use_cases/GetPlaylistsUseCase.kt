package com.muqp.feature_favorites.domain.use_cases

import com.muqp.feature_favorites.domain.PlaylistRepository
import com.muqp.feature_favorites.model.PlaylistUI
import javax.inject.Inject

class GetPlaylistsUseCase @Inject constructor(
    private val playlistRepository: PlaylistRepository
) {
    suspend operator fun invoke(): List<PlaylistUI> {
        return playlistRepository.getAllPlaylists()
    }
}