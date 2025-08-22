package com.muqp.beatbeat.details.domain.local.use_cases

import com.muqp.beatbeat.details.domain.local.FavoriteRepository
import com.muqp.beatbeat.details.model.ItemTrackUI
import javax.inject.Inject

class DeleteTrackUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    suspend operator fun invoke(itemTrackUI: ItemTrackUI) {
        favoriteRepository.deleteTrack(itemTrackUI)
    }
}