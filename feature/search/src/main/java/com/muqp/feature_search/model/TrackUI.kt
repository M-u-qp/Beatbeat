package com.muqp.feature_search.model

data class TrackUI(
    val headers: HeadersTrackUI,
    val results: List<ItemTrackUI>
)

data class HeadersTrackUI(
    val resultsCount: Int
)

data class ItemTrackUI(
    val id: String,
    val name: String,
    val duration: Int,
    val artistId: String,
    val artistName: String,
    val albumName: String,
    val albumId: String,
    val releaseDate: String,
    val albumImage: String,
    val audio: String,
    val audioDownload: String,
    val shareUrl: String,
    val image: String,
    val musicInfo: MusicInfoTrackUI,
    val audioDownloadAllowed: Boolean,
    val isFavorite: Boolean
)

data class MusicInfoTrackUI(
    val vocalInstrumental: String,
    val speed: String,
    val tags: TagsTrackUI,
    val instruments: List<String>
)

data class TagsTrackUI(
    val genres: List<String>
)