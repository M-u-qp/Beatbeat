package com.muqp.feature_search.domain.local.use_cases

import com.muqp.feature_search.domain.local.FavoriteRepository
import com.muqp.feature_search.model.ItemAlbumUI
import javax.inject.Inject

class InsertAlbumUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    suspend operator fun invoke(itemAlbumUI: ItemAlbumUI) {
        favoriteRepository.insertAlbum(itemAlbumUI)
    }
}