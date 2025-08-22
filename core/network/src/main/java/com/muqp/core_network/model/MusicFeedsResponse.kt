package com.muqp.core_network.model

data class MusicFeedsResponse(
    val headers: FeedsHeaders,
    val results: List<FeedItem>
)

data class FeedsHeaders(
    val results_count: Int
)

data class FeedItem(
    val id: String,
    val title: Lang,
    val link: String,
    val date_start: String,
    val date_end: String,
    val type: String,
    val text: Lang,
    val images: ImageSize
)

data class Lang(
    val en: String,
    val ru: String
)

data class ImageSize(
    val size996_350: String,
    val size315_111: String,
    val size600_211: String,
    val size470_165: String,
)
