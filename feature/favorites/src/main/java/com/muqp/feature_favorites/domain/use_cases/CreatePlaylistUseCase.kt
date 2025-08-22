package com.muqp.feature_favorites.domain.use_cases

import com.muqp.feature_favorites.domain.PlaylistRepository
import javax.inject.Inject

class CreatePlaylistUseCase @Inject constructor(
    private val playlistRepository: PlaylistRepository
) {
    suspend operator fun invoke(
        name: String,
        description: String?,
        coverImage: String? = null
    ) {
        playlistRepository.createPlaylist(name, description, coverImage)
    }
}