package com.muqp.feature_listen.domain.local.use_cases

import com.muqp.feature_listen.domain.local.FavoriteRepository
import com.muqp.feature_listen.model.ItemTrackUI
import javax.inject.Inject

class DeleteTrackUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    suspend operator fun invoke(itemTrackUI: ItemTrackUI) {
        favoriteRepository.deleteTrack(itemTrackUI)
    }
}