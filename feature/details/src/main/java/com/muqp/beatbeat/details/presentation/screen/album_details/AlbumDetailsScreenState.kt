package com.muqp.beatbeat.details.presentation.screen.album_details

import com.muqp.beatbeat.details.model.ItemAlbumUI

sealed class AlbumDetailsScreenState {
    data object Loading : AlbumDetailsScreenState()
    data class Success(val albumData: ItemAlbumUI? = null) : AlbumDetailsScreenState()
    data class Error(val message: String) : AlbumDetailsScreenState()
}