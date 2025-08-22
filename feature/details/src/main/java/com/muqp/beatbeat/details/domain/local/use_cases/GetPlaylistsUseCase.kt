package com.muqp.beatbeat.details.domain.local.use_cases

import com.muqp.beatbeat.details.domain.local.PlaylistRepository
import com.muqp.beatbeat.details.model.PlaylistUI
import javax.inject.Inject

class GetPlaylistsUseCase @Inject constructor(
    private val playlistRepository: PlaylistRepository
) {
    suspend operator fun invoke(): List<PlaylistUI> {
        return playlistRepository.getAllPlaylists()
    }
}