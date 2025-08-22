package com.muqp.feature_home.model

data class MusicFeedsUI(
    val headers: FeedsHeadersUI,
    val results: List<FeedItemUI>
)

data class FeedsHeadersUI(
    val resultsCount: Int
)

data class FeedItemUI(
    val id: String,
    val title: LangUI,
    val link: String,
    val dateStart: String,
    val dateEnd: String,
    val type: String,
    val text: LangUI,
    val images: ImageSizeUI
)

data class LangUI(
    val en: String,
    val ru: String
)

data class ImageSizeUI(
    val size996X350: String,
    val size315X111: String,
    val size600X211: String,
    val size470X165: String,
)