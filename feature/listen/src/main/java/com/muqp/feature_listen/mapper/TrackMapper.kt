package com.muqp.feature_listen.mapper

import com.muqp.core_database.database.entity.ItemTrackEntity
import com.muqp.core_network.model.SearchItemTrack
import com.muqp.core_network.model.SearchResultToTrackResponse
import com.muqp.feature_listen.model.ItemTrackUI
import com.muqp.feature_listen.model.TrackUI

object TrackMapper {
    fun SearchResultToTrackResponse.toTrackUI(): TrackUI {
        return TrackUI(
            results = this.results.map { it.toItemTrackUI() }
        )
    }

    fun SearchItemTrack.toItemTrackUI(): ItemTrackUI {
        return ItemTrackUI(
            id = this.id ?: "",
            name = this.name ?: "",
            duration = this.duration ?: 0,
            albumId = this.album_id ?: "",
            albumName = this.album_name ?: "",
            artistId = this.artist_id ?: "",
            artistName = this.artist_name ?: "",
            albumImage = this.album_image ?: "",
            releaseDate = this.releasedate ?: "",
            audio = this.audio ?: "",
            audioDownload = this.audiodownload ?: "",
            shareUrl = this.shareurl ?: "",
            image = this.image ?: "",
            audioDownloadAllowed = this.audiodownload_allowed ?: false,
            isFavorite = false
        )
    }

    fun ItemTrackUI.toItemTrackEntity(): ItemTrackEntity {
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

    fun ItemTrackEntity.toItemTrackUI(): ItemTrackUI {
        return ItemTrackUI(
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