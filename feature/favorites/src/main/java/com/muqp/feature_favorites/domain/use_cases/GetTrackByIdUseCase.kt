package com.muqp.feature_favorites.domain.use_cases

import com.muqp.feature_favorites.domain.FavoriteRepository
import com.muqp.feature_favorites.model.ItemTrack
import javax.inject.Inject

class GetTrackByIdUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    suspend operator fun invoke(trackId: String): ItemTrack? {
        return favoriteRepository.getTrackById(trackId)
    }
}