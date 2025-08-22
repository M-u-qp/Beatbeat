package com.muqp.beatbeat.details.presentation.screen.artist_details

import com.muqp.beatbeat.details.model.AlbumUI
import com.muqp.beatbeat.details.model.ItemTrackUI

sealed class ArtistDetailsScreenState {
    data object Loading : ArtistDetailsScreenState()
    data class Success(
        val albums: AlbumUI? = null,
        val popularTracks: List<ItemTrackUI>
    ) : ArtistDetailsScreenState()

    data class Error(val message: String) : ArtistDetailsScreenState()
}