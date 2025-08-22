package com.muqp.feature_favorites.domain.use_cases

import com.muqp.feature_favorites.domain.FavoriteRepository
import com.muqp.feature_favorites.model.ItemAlbum
import javax.inject.Inject

class GetAlbumByIdUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    suspend operator fun invoke(albumId: String): ItemAlbum? {
        return favoriteRepository.getAlbumById(albumId)
    }
}