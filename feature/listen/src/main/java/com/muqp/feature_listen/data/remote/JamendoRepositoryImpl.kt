package com.muqp.feature_listen.data.remote

import com.muqp.beatbeat.listen.BuildConfig
import com.muqp.core_network.api.JamendoApi
import com.muqp.feature_listen.domain.remote.JamendoRepository
import com.muqp.feature_listen.mapper.TrackMapper.toTrackUI
import com.muqp.feature_listen.model.TrackUI
import java.io.IOException
import javax.inject.Inject

class JamendoRepositoryImpl @Inject constructor(
    private val jamendoApi: JamendoApi
): JamendoRepository {
    override suspend fun getTrackCollections(
        acoEle: String?,
        gender: String?,
        speed: List<String>?
    ): TrackUI {
        return try {
            val response = jamendoApi.getSearchResultToTrack(
                clientId = BuildConfig.CLIENT_ID,
                limit = RESPONSE_ITEM_SIZE.toString(),
                acoEle = acoEle,
                gender = gender,
                speed = speed
            )
            if (response.isSuccessful && response.body() != null) {
                response.body()!!.toTrackUI()
            } else {
                throw IOException("${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            throw IOException("${e.message}")
        }
    }

    companion object {
        private const val RESPONSE_ITEM_SIZE = 20
    }
}