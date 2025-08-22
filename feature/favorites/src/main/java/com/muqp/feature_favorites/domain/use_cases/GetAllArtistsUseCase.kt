package com.muqp.feature_favorites.domain.use_cases

import com.muqp.feature_favorites.domain.FavoriteRepository
import com.muqp.feature_favorites.model.ItemArtist
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllArtistsUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    operator fun invoke(): Flow<List<ItemArtist>> {
        return favoriteRepository.getAllArtists()
    }
}