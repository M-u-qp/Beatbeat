package com.muqp.feature_favorites.domain.use_cases

import com.muqp.feature_favorites.domain.FavoriteRepository
import com.muqp.feature_favorites.model.ItemArtist
import javax.inject.Inject

class DeleteArtistUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    suspend operator fun invoke(itemArtist: ItemArtist) {
        favoriteRepository.deleteArtist(itemArtist)
    }
}