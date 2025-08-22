package com.muqp.feature_search.mapper

import com.muqp.core_database.database.entity.ItemTrackEntity
import com.muqp.core_network.model.MusicInfoTrack
import com.muqp.core_network.model.SearchHeadersTrack
import com.muqp.core_network.model.SearchItemTrack
import com.muqp.core_network.model.SearchResultToTrackResponse
import com.muqp.core_network.model.TagsTrack
import com.muqp.feature_search.model.HeadersTrackUI
import com.muqp.feature_search.model.ItemTrackUI
import com.muqp.feature_search.model.MusicInfoTrackUI
import com.muqp.feature_search.model.TagsTrackUI
import com.muqp.feature_search.model.TrackUI

object TrackMapper {
    fun SearchResultToTrackResponse.toTrackUI(): TrackUI {
        return TrackUI(
            headers = this.headers.toHeadersTrackUI(),
            results = this.results.map { it.toItemTrackUI() }
        )
    }

    private fun SearchHeadersTrack.toHeadersTrackUI(): HeadersTrackUI {
        return HeadersTrackUI(
            resultsCount = this.results_count ?: 0
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
            musicInfo = this.musicinfo?.toMusicInfoTrackUI() ?: MusicInfoTrackUI(
                vocalInstrumental = "",
                speed = "",
                tags = TagsTrackUI(
                    emptyList()
                ),
                instruments = emptyList()
            ),
            audioDownloadAllowed = this.audiodownload_allowed ?: false,
            isFavorite = false
        )
    }

    private fun MusicInfoTrack.toMusicInfoTrackUI(): MusicInfoTrackUI {
        return MusicInfoTrackUI(
            vocalInstrumental = this.vocalinstrumental ?: "",
            speed = this.speed ?: "",
            tags = this.tags?.toTagsTrackUI() ?: TagsTrackUI(genres = emptyList()),
            instruments = this.instruments ?: emptyList()
        )
    }

    private fun TagsTrack.toTagsTrackUI(): TagsTrackUI {
        return TagsTrackUI(
            genres = this.genres ?: emptyList()
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
            musicInfo = MusicInfoTrackUI(
                vocalInstrumental = "",
                speed = "",
                tags = TagsTrackUI(
                    emptyList()
                ),
                instruments = emptyList()
            ),
            audioDownloadAllowed = this.audioDownloadAllowed,
            isFavorite = this.isFavorite
        )
    }
}