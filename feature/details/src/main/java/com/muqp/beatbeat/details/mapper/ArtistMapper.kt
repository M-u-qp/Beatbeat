package com.muqp.beatbeat.details.mapper

import com.muqp.beatbeat.details.model.ItemArtistUI
import com.muqp.core_database.database.entity.ItemArtistsEntity

object ArtistMapper {
    fun ItemArtistUI.toItemArtistEntity(): ItemArtistsEntity {
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

    fun ItemArtistsEntity.toArtistUI(): ItemArtistUI {
        return ItemArtistUI(
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