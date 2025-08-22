package com.muqp.core_network.model

data class SearchResultToArtistResponse(
    val headers: SearchHeadersArtist,
    val results: List<SearchItemArtist>
)

data class SearchHeadersArtist(
    val results_count: Int?
)

data class SearchItemArtist(
    val id: String?,
    val name: String?,
    val website: String?,
    val joindate: String?,
    val image: String?,
    val shareurl: String?
)