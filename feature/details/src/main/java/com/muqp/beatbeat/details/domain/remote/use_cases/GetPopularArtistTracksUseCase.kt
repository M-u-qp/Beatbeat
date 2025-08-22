package com.muqp.beatbeat.details.domain.remote.use_cases

import com.muqp.beatbeat.details.domain.remote.JamendoRepository
import com.muqp.beatbeat.details.model.ItemTrackUI
import javax.inject.Inject

class GetPopularArtistTracksUseCase @Inject constructor(
    private val jamendoRepository: JamendoRepository
) {
    suspend operator fun invoke(artistId: Int): List<ItemTrackUI> {
        return jamendoRepository.getPopularArtistTracks(artistId)
    }
}