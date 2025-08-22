package com.muqp.feature_listen.presentation.screen

import com.muqp.feature_listen.model.TrackUI

sealed class ListenScreenState {

    data object Loading : ListenScreenState()

    data class Success(
        val acousticTrackData: TrackUI,
        val electrTrackData: TrackUI,
        val femaleTrackData: TrackUI,
        val maleTrackData: TrackUI,
        val slowTrackData: TrackUI,
        val fastTrackData: TrackUI,
    ) : ListenScreenState()

    data class Error(val message: String) : ListenScreenState()
}