package com.muqp.feature_search.domain.local.use_cases

import com.muqp.feature_search.domain.local.FavoriteRepository
import com.muqp.feature_search.model.ItemArtistUI
import javax.inject.Inject

class GetArtistByIdUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    suspend operator fun invoke(artistId: String): ItemArtistUI? {
        return favoriteRepository.getArtistById(artistId)
    }
}