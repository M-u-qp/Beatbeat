package com.muqp.beatbeat.details.mapper

import com.muqp.beatbeat.details.mapper.TrackMapper.toItemTrackUI
import com.muqp.beatbeat.details.model.AlbumUI
import com.muqp.beatbeat.details.model.ItemAlbumUI
import com.muqp.core_database.database.entity.ItemAlbumEntity
import com.muqp.core_network.model.AlbumDetailsResponse
import com.muqp.core_network.model.AllArtistAlbumsResponse
import com.muqp.core_network.model.ItemAlbumResponse
import com.muqp.core_network.model.ItemAlbumWithoutTracksResponse

object AlbumMapper {
    fun AlbumDetailsResponse.toAlbumUI(): AlbumUI {
        return AlbumUI(
            results = this.results.map { it.toItemAlbumUI() }
        )
    }

    private fun ItemAlbumResponse.toItemAlbumUI(): ItemAlbumUI {
        return ItemAlbumUI(
            id = this.id,
            name = this.name,
            releaseDate = this.releasedate,
            artistId = this.artist_id,
            artistName = this.artist_name,
            image = this.image,
            tracks = this.tracks.map { it.toItemTrackUI() },
            isFavorite = false
        )
    }

    fun AllArtistAlbumsResponse.toAlbumUI(): AlbumUI {
        return AlbumUI(
            results = this.results.map { it.toItemAlbumUI() }
        )
    }

    private fun ItemAlbumWithoutTracksResponse.toItemAlbumUI(): ItemAlbumUI {
        return ItemAlbumUI(
            id = this.id,
            name = this.name,
            releaseDate = this.releasedate,
            artistId = this.artist_id,
            artistName = this.artist_name,
            image = this.image,
            tracks = listOf(),
            isFavorite = false
        )
    }

    fun ItemAlbumUI.toItemAlbumEntity(): ItemAlbumEntity {
        return ItemAlbumEntity(
            id = this.id,
            name = this.name,
            releaseDate = this.releaseDate,
            artistId = this.artistId,
            artistName = this.artistName,
            image = this.image,
            zip = "",
            shareUrl = "this.shareUrl",
            zipAllowed = false,
            isFavorite = this.isFavorite
        )
    }

    fun ItemAlbumEntity.toItemAlbumUI(): ItemAlbumUI {
        return ItemAlbumUI(
            id = this.id,
            name = this.name,
            releaseDate = this.releaseDate,
            artistId = this.artistId,
            artistName = this.artistName,
            image = this.image,
            tracks = listOf(),
            isFavorite = this.isFavorite
        )
    }
}