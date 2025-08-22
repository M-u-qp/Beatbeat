package com.muqp.feature_listen.model

data class PlaylistUI(
    val id: Long? = null,
    val name: String,
    val description: String?,
    val createdAt: Long? = null,
    val coverImage: String?,
    val trackCount: Int = 0
)