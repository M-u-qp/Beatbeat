package com.muqp.feature_search.mapper

import com.muqp.core_database.database.entity.ItemArtistsEntity
import com.muqp.core_network.model.SearchHeadersArtist
import com.muqp.core_network.model.SearchItemArtist
import com.muqp.core_network.model.SearchResultToArtistResponse
import com.muqp.feature_search.model.ArtistUI
import com.muqp.feature_search.model.HeadersArtistUI
import com.muqp.feature_search.model.ItemArtistUI

object ArtistMapper {
    fun SearchResultToArtistResponse.toArtistUI(): ArtistUI {
        return ArtistUI(
            headers = this.headers.toHeadersArtistUI(),
            results = this.results.map { it.toItemArtistUI() }
        )
    }

    private fun SearchHeadersArtist.toHeadersArtistUI(): HeadersArtistUI {
        return HeadersArtistUI(
            resultsCount = this.results_count ?: 0
        )
    }

    fun SearchItemArtist.toItemArtistUI(): ItemArtistUI {
        return ItemArtistUI(
            id = this.id ?: "",
            name = this.name ?: "",
            website = this.website ?: "",
            joinDate = this.joindate ?: "",
            image = this.image ?: "",
            shareUrl = this.shareurl ?: "",
            isFavorite = false
        )
    }

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

    fun ItemArtistsEntity.toItemArtistUI(): ItemArtistUI {
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