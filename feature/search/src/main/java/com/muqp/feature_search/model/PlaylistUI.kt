package com.muqp.feature_search.model

data class PlaylistUI(
    val id: Long? = null,
    val name: String,
    val description: String?,
    val createdAt: Long? = null,
    val coverImage: String?,
    val trackCount: Int = 0
)
