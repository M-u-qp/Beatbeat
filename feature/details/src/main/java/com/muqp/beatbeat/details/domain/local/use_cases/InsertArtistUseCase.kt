package com.muqp.beatbeat.details.domain.local.use_cases

import com.muqp.beatbeat.details.domain.local.FavoriteRepository
import com.muqp.beatbeat.details.model.ItemArtistUI
import javax.inject.Inject

class InsertArtistUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    suspend operator fun invoke(itemArtistUI: ItemArtistUI) {
        favoriteRepository.insertArtist(itemArtistUI)
    }
}