package com.muqp.beatbeat.details.mapper

import com.muqp.beatbeat.details.model.ItemTrackUI
import com.muqp.core_database.database.entity.ItemTrackEntity
import com.muqp.core_network.model.ItemTracksResponse
import com.muqp.core_network.model.SearchItemTrack

object TrackMapper {
    fun SearchItemTrack.toItemTrackUI(): ItemTrackUI {
        return ItemTrackUI(
            id = this.id ?: "",
            name = this.name ?: "",
            duration = this.duration.toString(),
            audio = this.audio ?: "",
            audioDownload = this.audiodownload ?: "",
            audioDownloadAllowed = this.audiodownload_allowed ?: false,
            isFavorite = false
        )
    }

    fun ItemTracksResponse.toItemTrackUI(): ItemTrackUI {
        return ItemTrackUI(
            id = this.id,
            name = this.name,
            duration = this.duration,
            audio = this.audio,
            audioDownload = this.audiodownload,
            audioDownloadAllowed = this.audiodownload_allowed,
            isFavorite = false
        )
    }

    fun ItemTrackUI.toItemTrackEntity(): ItemTrackEntity {
        return ItemTrackEntity(
            id = this.id,
            name = this.name,
            duration = this.duration.toInt(),
            albumId = "",
            albumName = "",
            artistId = "",
            artistName = "",
            albumImage = "",
            releaseDate = "",
            audio = this.audio,
            audioDownload = this.audioDownload,
            shareUrl = "",
            image = "",
            audioDownloadAllowed = this.audioDownloadAllowed,
            isFavorite = this.isFavorite
        )
    }

    fun ItemTrackEntity.toItemTrackUI(): ItemTrackUI {
        return ItemTrackUI(
            id = this.id,
            name = this.name,
            duration = this.duration.toString(),
            audio = this.audio,
            audioDownload = this.audioDownload,
            audioDownloadAllowed = this.audioDownloadAllowed,
            isFavorite = this.isFavorite
        )
    }
}