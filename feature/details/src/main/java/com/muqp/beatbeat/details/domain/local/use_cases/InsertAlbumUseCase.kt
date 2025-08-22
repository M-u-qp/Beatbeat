package com.muqp.beatbeat.details.domain.local.use_cases

import com.muqp.beatbeat.details.domain.local.FavoriteRepository
import com.muqp.beatbeat.details.model.ItemAlbumUI
import javax.inject.Inject

class InsertAlbumUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    suspend operator fun invoke(itemAlbumUI: ItemAlbumUI) {
        favoriteRepository.insertAlbum(itemAlbumUI)
    }
}