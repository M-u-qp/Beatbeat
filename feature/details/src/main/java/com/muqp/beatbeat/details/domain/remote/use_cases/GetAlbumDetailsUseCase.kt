package com.muqp.beatbeat.details.domain.remote.use_cases

import com.muqp.beatbeat.details.domain.remote.JamendoRepository
import com.muqp.beatbeat.details.model.AlbumUI
import javax.inject.Inject

class GetAlbumDetailsUseCase @Inject constructor(
    private val jamendoRepository: JamendoRepository
) {
    suspend operator fun invoke(albumId: Int): AlbumUI {
        return jamendoRepository.getAlbumDetails(albumId)
    }
}