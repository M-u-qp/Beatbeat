package com.muqp.beatbeat.details.domain.local.use_cases

import com.muqp.beatbeat.details.domain.local.FavoriteRepository
import com.muqp.beatbeat.details.model.ItemTrackUI
import javax.inject.Inject

class GetTrackByIdUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    suspend operator fun invoke(trackId: String): ItemTrackUI? {
        return favoriteRepository.getTrackById(trackId)
    }
}