package com.muqp.feature_favorites.domain.use_cases

import com.muqp.feature_favorites.domain.PlaylistRepository
import com.muqp.feature_favorites.model.ItemTrack
import com.muqp.feature_favorites.model.PlaylistUI
import javax.inject.Inject

class GetPlaylistWithTracksUseCase @Inject constructor(
    private val playlistRepository: PlaylistRepository
){
    suspend operator fun invoke(playlistId: Long): Pair<PlaylistUI, List<ItemTrack>> {
        return playlistRepository.getPlaylistWithTracks(playlistId)
    }
}