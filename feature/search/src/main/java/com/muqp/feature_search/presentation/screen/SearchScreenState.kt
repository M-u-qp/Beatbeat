package com.muqp.feature_search.presentation.screen

import com.muqp.feature_search.model.AlbumUI
import com.muqp.feature_search.model.ArtistUI
import com.muqp.feature_search.model.TrackUI

sealed class SearchScreenState {
    data object NoInputData : SearchScreenState()
    data object Loading : SearchScreenState()
    data object NotFound : SearchScreenState()
    data class Success(
        val trackData: TrackUI,
        val albumData: AlbumUI,
        val artistData: ArtistUI
    ) : SearchScreenState()

    data class Error(val message: String) : SearchScreenState()
}