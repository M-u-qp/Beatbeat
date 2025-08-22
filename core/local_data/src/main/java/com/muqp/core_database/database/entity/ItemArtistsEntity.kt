package com.muqp.core_database.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "artists")
data class ItemArtistsEntity(
    @PrimaryKey val id: String,
    val name: String,
    val website: String,
    val joinDate: String,
    val image: String,
    val shareUrl: String,
    val isFavorite: Boolean
)
