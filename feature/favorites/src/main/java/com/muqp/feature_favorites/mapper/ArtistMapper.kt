package com.muqp.feature_favorites.mapper

import com.muqp.core_database.database.entity.ItemArtistsEntity
import com.muqp.feature_favorites.model.ItemArtist

object ArtistMapper {
    fun ItemArtistsEntity.toItemArtist(): ItemArtist {
        return ItemArtist(
            id = this.id,
            name = this.name,
            website = this.website,
            joinDate = this.joinDate,
            image = this.image,
            shareUrl = this.shareUrl,
            isFavorite = this.isFavorite
        )
    }

    fun ItemArtist.toItemArtistEntity(): ItemArtistsEntity {
        return ItemArtistsEntity(
            id = this.id,
            name = this.name,
            website = this.website,
            joinDate = this.joinDate,
            image = this.image,
            shareUrl = this.shareUrl,
            isFavorite = this.isFavorite
        )
    }
}