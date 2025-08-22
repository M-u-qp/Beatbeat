package com.muqp.feature_listen.domain.remote.use_cases

import com.muqp.feature_listen.domain.remote.JamendoRepository
import com.muqp.feature_listen.model.TrackUI
import javax.inject.Inject

class GetAcousticCollectionUseCase @Inject constructor(
    private val jamendoRepository: JamendoRepository
) {
    suspend operator fun invoke(acoEle: String): TrackUI {
        return jamendoRepository.getTrackCollections(
            acoEle = acoEle, gender = null, speed = null
        )
    }
}