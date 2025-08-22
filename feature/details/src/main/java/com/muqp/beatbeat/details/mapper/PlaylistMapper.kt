package com.muqp.beatbeat.details.mapper

import com.muqp.beatbeat.details.model.PlaylistTrackCrossRefUI
import com.muqp.beatbeat.details.model.PlaylistUI
import com.muqp.core_database.database.entity.PlaylistEntity
import com.muqp.core_database.database.entity.PlaylistTrackCrossRef

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

    fun PlaylistTrackCrossRefUI.toPlaylistTrackCrossRef(): PlaylistTrackCrossRef {
        return PlaylistTrackCrossRef(
            playlistId = this.playlistId,
            trackId = this.trackId,
            position = this.position
        )
    }
}