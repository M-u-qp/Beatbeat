package com.muqp.feature_favorites.mapper

import com.muqp.core_database.database.entity.PlaylistEntity
import com.muqp.core_database.database.entity.PlaylistTrackCrossRef
import com.muqp.feature_favorites.model.PlaylistTrackCrossRefUI
import com.muqp.feature_favorites.model.PlaylistUI

object PlaylistMapper {
    fun PlaylistEntity.toPlaylistUI(): PlaylistUI {
        return PlaylistUI(
            id = this.id,
            name = this.name,
            description = this.description,
            createdAt = this.createdAt,
            coverImage = this.coverImage
        )
    }

    fun PlaylistUI.toPlaylistEntity(): PlaylistEntity {
        return PlaylistEntity(
            name = this.name,
            description = this.description,
            coverImage = this.coverImage
        )
    }

    fun PlaylistTrackCrossRef.toPlaylistTrackCrossRefUI(): PlaylistTrackCrossRefUI {
        return PlaylistTrackCrossRefUI(
            playlistId = this.playlistId,
            trackId = this.trackId,
            position = this.position,
            addedAt = this.addedAt
        )
    }

    fun PlaylistTrackCrossRefUI.toPlaylistTrackCrossRef(): PlaylistTrackCrossRef {
        return PlaylistTrackCrossRef(
            playlistId = this.playlistId,
            trackId = this.trackId,
            position = this.position
        )
    }
}