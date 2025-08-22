package com.muqp.feature_favorites.mapper

import com.muqp.core_database.database.entity.ItemAlbumEntity
import com.muqp.feature_favorites.model.ItemAlbum

object AlbumMapper {
    fun ItemAlbumEntity.toItemAlbum(): ItemAlbum {
        return ItemAlbum(
            id = this.id,
            name = this.name,
            releaseDate = this.releaseDate,
            artistId = this.artistId,
            artistName = this.artistName,
            image = this.image,
            zip = this.zip,
            shareUrl = this.shareUrl,
            zipAllowed = this.zipAllowed,
            isFavorite = this.isFavorite
        )
    }

    fun ItemAlbum.toItemAlbumEntity(): ItemAlbumEntity {
        return ItemAlbumEntity(
            id = this.id,
            name = this.name,
            releaseDate = this.releaseDate,
            artistId = this.artistId,
            artistName = this.artistName,
            image = this.image,
            zip = this.zip,
            shareUrl = this.shareUrl,
            zipAllowed = this.zipAllowed,
            isFavorite = this.isFavorite
        )
    }
}