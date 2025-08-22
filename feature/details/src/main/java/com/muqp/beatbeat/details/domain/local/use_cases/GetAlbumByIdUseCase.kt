package com.muqp.beatbeat.details.domain.local.use_cases

import com.muqp.beatbeat.details.domain.local.FavoriteRepository
import com.muqp.beatbeat.details.model.ItemAlbumUI
import javax.inject.Inject

class GetAlbumByIdUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    suspend operator fun invoke(albumId: String): ItemAlbumUI? {
        return favoriteRepository.getAlbumById(albumId)
    }
}