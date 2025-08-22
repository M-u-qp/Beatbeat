package com.muqp.feature_search.mapper

import com.muqp.core_database.database.entity.ItemAlbumEntity
import com.muqp.core_network.model.SearchHeadersAlbum
import com.muqp.core_network.model.SearchItemAlbum
import com.muqp.core_network.model.SearchResultToAlbumResponse
import com.muqp.feature_search.model.AlbumUI
import com.muqp.feature_search.model.HeadersAlbumUI
import com.muqp.feature_search.model.ItemAlbumUI

object AlbumMapper {
    fun SearchResultToAlbumResponse.toAlbumUI(): AlbumUI {
        return AlbumUI(
            headers = this.headers.toHeadersAlbumUI(),
            results = this.results.map { it.toItemAlbumUI() }
        )
    }

    private fun SearchHeadersAlbum.toHeadersAlbumUI(): HeadersAlbumUI {
        return HeadersAlbumUI(
            resultsCount = this.results_count ?: 0
        )
    }

    fun SearchItemAlbum.toItemAlbumUI(): ItemAlbumUI {
        return ItemAlbumUI(
            id = this.id ?: "",
            name = this.name ?: "",
            releaseDate = this.releasedate ?: "",
            artistId = this.artist_id ?: "",
            artistName = this.artist_name ?: "",
            image = this.image ?: "",
            zip = this.zip ?: "",
            shareUrl = this.shareurl ?: "",
            zipAllowed = this.zip_allowed ?: false,
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
            zip = this.zip,
            shareUrl = this.shareUrl,
            zipAllowed = this.zipAllowed,
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
            zip = this.zip,
            shareUrl = this.shareUrl,
            zipAllowed = this.zipAllowed,
            isFavorite = this.isFavorite
        )
    }
}