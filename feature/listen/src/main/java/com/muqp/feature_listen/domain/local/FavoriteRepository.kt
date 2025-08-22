package com.muqp.feature_listen.domain.local

import com.muqp.feature_listen.model.ItemTrackUI

interface FavoriteRepository {
    suspend fun getTrackById(trackId: String): ItemTrackUI?
    suspend fun insertTrack(itemTrackUI: ItemTrackUI)
    suspend fun deleteTrack(itemTrackUI: ItemTrackUI)
}