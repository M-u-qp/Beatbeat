package com.muqp.core_network.model

data class SearchResultToAlbumResponse(
    val headers: SearchHeadersAlbum,
    val results: List<SearchItemAlbum>
)

data class SearchHeadersAlbum(
    val results_count: Int?
)

data class SearchItemAlbum(
    val id: String?,
    val name: String?,
    val releasedate: String?,
    val artist_id: String?,
    val artist_name: String?,
    val image: String?,
    val zip: String?,
    val shareurl: String?,
    val zip_allowed: Boolean?
)
