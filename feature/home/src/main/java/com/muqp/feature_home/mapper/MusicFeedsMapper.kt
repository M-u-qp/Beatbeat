package com.muqp.feature_home.mapper

import com.muqp.core_network.model.FeedItem
import com.muqp.core_network.model.FeedsHeaders
import com.muqp.core_network.model.ImageSize
import com.muqp.core_network.model.Lang
import com.muqp.core_network.model.MusicFeedsResponse
import com.muqp.feature_home.model.FeedItemUI
import com.muqp.feature_home.model.FeedsHeadersUI
import com.muqp.feature_home.model.ImageSizeUI
import com.muqp.feature_home.model.LangUI
import com.muqp.feature_home.model.MusicFeedsUI

object MusicFeedsMapper {
    fun MusicFeedsResponse.toMusicFeedsUi(): MusicFeedsUI {
        return MusicFeedsUI(
            headers = this.headers.toFeedHeadersUI(),
            results = this.results.map {
                it.toFeedItemUI()
            }
        )
    }

    private fun FeedsHeaders.toFeedHeadersUI(): FeedsHeadersUI {
        return FeedsHeadersUI(
            resultsCount = this.results_count
        )
    }

     fun FeedItem.toFeedItemUI(): FeedItemUI {
        return FeedItemUI(
            id = this.id,
            title = this.title.toLangUI(),
            link = this.link,
            dateStart = this.date_start,
            dateEnd = this.date_end,
            type = this.type,
            text = this.text.toLangUI(),
            images = this.images.toImageSizeUI()
        )
    }

    private fun Lang.toLangUI(): LangUI {
        return LangUI(
            en = this.en,
            ru = this.ru
        )
    }

    private fun ImageSize.toImageSizeUI(): ImageSizeUI {
        return ImageSizeUI(
            size996X350 = this.size996_350,
            size600X211 = this.size600_211,
            size470X165 = this.size470_165,
            size315X111 = this.size315_111
        )
    }
}