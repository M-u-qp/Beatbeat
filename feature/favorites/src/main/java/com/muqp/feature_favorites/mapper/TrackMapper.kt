package com.muqp.feature_favorites.mapper

import com.muqp.core_database.database.entity.ItemTrackEntity
import com.muqp.feature_favorites.model.ItemTrack

object TrackMapper {
    fun ItemTrackEntity.toItemTrack(): ItemTrack {
        return ItemTrack(
            id = this.id,
            name = this.name,
            duration = this.duration,
            albumId = this.albumId,
            albumName = this.albumName,
            artistId = this.artistId,
            artistName = this.artistName,
            albumImage = this.albumImage,
            releaseDate = this.releaseDate,
            audio = this.audio,
            audioDownload = this.audioDownload,
            shareUrl = this.shareUrl,
            image = this.image,
            audioDownloadAllowed = this.audioDownloadAllowed,
            isFavorite = this.isFavorite
        )
    }

    fun ItemTrack.toItemTrackEntity(): ItemTrackEntity {
        return ItemTrackEntity(
            id = this.id,
            name = this.name,
            duration = this.duration,
            albumId = this.albumId,
            albumName = this.albumName,
            artistId = this.artistId,
            artistName = this.artistName,
            albumImage = this.albumImage,
            releaseDate = this.releaseDate,
            audio = this.audio,
            audioDownload = this.audioDownload,
            shareUrl = this.shareUrl,
            image = this.image,
            audioDownloadAllowed = this.audioDownloadAllowed,
            isFavorite = this.isFavorite
        )
    }
}