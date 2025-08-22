package com.muqp.feature_favorites.domain.use_cases

import com.muqp.feature_favorites.domain.FavoriteRepository
import com.muqp.feature_favorites.model.ItemArtist
import javax.inject.Inject

class GetArtistByIdUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    suspend operator fun invoke(artistId: String): ItemArtist? {
        return favoriteRepository.getArtistById(artistId)
    }
}