package com.muqp.feature_listen.data.local

import com.muqp.core_database.database.dao.TrackDao
import com.muqp.feature_listen.domain.local.FavoriteRepository
import com.muqp.feature_listen.mapper.TrackMapper.toItemTrackEntity
import com.muqp.feature_listen.mapper.TrackMapper.toItemTrackUI
import com.muqp.feature_listen.model.ItemTrackUI
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(
    private val trackDao: TrackDao
) : FavoriteRepository {
    override suspend fun getTrackById(trackId: String): ItemTrackUI? {
        return trackDao.getTrackById(trackId)?.toItemTrackUI()
    }

    override suspend fun insertTrack(itemTrackUI: ItemTrackUI) {
        trackDao.insertTrack(itemTrackUI.toItemTrackEntity())
    }

    override suspend fun deleteTrack(itemTrackUI: ItemTrackUI) {
        trackDao.deleteTrack(itemTrackUI.toItemTrackEntity())
    }
}