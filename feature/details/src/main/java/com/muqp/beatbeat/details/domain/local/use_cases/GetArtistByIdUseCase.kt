package com.muqp.beatbeat.details.domain.local.use_cases

import com.muqp.beatbeat.details.domain.local.FavoriteRepository
import com.muqp.beatbeat.details.model.ItemArtistUI
import javax.inject.Inject

class GetArtistByIdUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    suspend operator fun invoke(artistId: String): ItemArtistUI? {
        return favoriteRepository.getArtistById(artistId)
    }
}