package com.muqp.core_database.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "albums")
data class ItemAlbumEntity(
    @PrimaryKey val id: String,
    val name: String,
    val releaseDate: String,
    val artistId: String,
    val artistName: String,
    val image: String,
    val zip: String,
    val shareUrl: String,
    val zipAllowed: Boolean,
    val isFavorite: Boolean
)
