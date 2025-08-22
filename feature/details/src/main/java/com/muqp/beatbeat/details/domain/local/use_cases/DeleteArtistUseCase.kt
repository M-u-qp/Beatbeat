package com.muqp.beatbeat.details.domain.local.use_cases

import com.muqp.beatbeat.details.domain.local.FavoriteRepository
import com.muqp.beatbeat.details.model.ItemArtistUI
import javax.inject.Inject

class DeleteArtistUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    suspend operator fun invoke(itemArtistUI: ItemArtistUI) {
        favoriteRepository.deleteArtist(itemArtistUI)
    }
}