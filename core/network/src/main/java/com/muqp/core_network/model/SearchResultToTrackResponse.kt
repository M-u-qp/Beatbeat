package com.muqp.core_network.model

data class SearchResultToTrackResponse(
    val headers: SearchHeadersTrack,
    val results: List<SearchItemTrack>
)

data class SearchHeadersTrack(
    val results_count: Int?
)

data class SearchItemTrack(
    val id: String?,
    val name: String?,
    val duration: Int?,
    val artist_id: String?,
    val artist_name: String?,
    val album_name: String?,
    val album_id: String?,
    val releasedate: String?,
    val album_image: String?,
    val audio: String?,
    val audiodownload: String?,
    val shareurl: String?,
    val image: String?,
    val musicinfo: MusicInfoTrack?,
    val audiodownload_allowed: Boolean?
)

data class MusicInfoTrack(
    val vocalinstrumental: String?,
    val speed: String?,
    val tags: TagsTrack?,
    val instruments: List<String>?
)

data class TagsTrack(
    val genres: List<String>?
)