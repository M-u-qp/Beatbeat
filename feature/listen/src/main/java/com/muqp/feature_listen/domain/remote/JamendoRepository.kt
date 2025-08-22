package com.muqp.feature_listen.domain.remote

import com.muqp.feature_listen.model.TrackUI

interface JamendoRepository {

    suspend fun getTrackCollections(
        acoEle: String?,
        gender: String?,
        speed: List<String>?
    ): TrackUI
}