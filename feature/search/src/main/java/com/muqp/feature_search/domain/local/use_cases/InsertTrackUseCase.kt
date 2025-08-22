package com.muqp.feature_search.domain.local.use_cases

import com.muqp.feature_search.domain.local.FavoriteRepository
import com.muqp.feature_search.model.ItemTrackUI
import javax.inject.Inject

class InsertTrackUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    suspend operator fun invoke(itemTrackUI: ItemTrackUI) {
        favoriteRepository.insertTrack(itemTrackUI)
    }
}