package com.muqp.beatbeat.details.domain.remote.use_cases

import com.muqp.beatbeat.details.domain.remote.JamendoRepository
import com.muqp.beatbeat.details.model.AlbumUI
import javax.inject.Inject

class GetAllArtistAlbumsUseCase @Inject constructor(
    private val jamendoRepository: JamendoRepository
) {
    suspend operator fun invoke(artistId: String): AlbumUI {
        return jamendoRepository.getAllArtistAlbums(artistId)
    }
}