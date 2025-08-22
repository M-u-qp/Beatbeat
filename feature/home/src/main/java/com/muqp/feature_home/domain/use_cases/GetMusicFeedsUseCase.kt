package com.muqp.feature_home.domain.use_cases

import androidx.paging.PagingSource
import com.muqp.feature_home.domain.JamendoRepository
import com.muqp.feature_home.model.FeedItemUI
import javax.inject.Inject

class GetMusicFeedsUseCase @Inject constructor(
    private val jamendoRepository: JamendoRepository
) {
    operator fun invoke(): PagingSource<Int, FeedItemUI> {
        return jamendoRepository.getMusicFeeds()
    }
}