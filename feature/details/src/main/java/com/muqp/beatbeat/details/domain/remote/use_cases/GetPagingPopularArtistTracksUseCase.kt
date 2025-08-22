package com.muqp.beatbeat.details.domain.remote.use_cases

import androidx.paging.PagingSource
import com.muqp.beatbeat.details.domain.remote.JamendoRepository
import com.muqp.beatbeat.details.model.ItemTrackUI
import javax.inject.Inject

class GetPagingPopularArtistTracksUseCase @Inject constructor(
    private val jamendoRepository: JamendoRepository
) {
    operator fun invoke(artistId: Int): PagingSource<Int, ItemTrackUI> {
        return jamendoRepository.getPagingPopularArtistTracks(artistId)
    }
}