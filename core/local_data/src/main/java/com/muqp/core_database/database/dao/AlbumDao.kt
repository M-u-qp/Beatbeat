package com.muqp.core_database.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.muqp.core_database.database.entity.ItemAlbumEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbum(albumEntity: ItemAlbumEntity)

    @Delete
    suspend fun deleteAlbum(albumEntity: ItemAlbumEntity)

    @Query("SELECT * FROM albums")
    fun getAllAlbums(): Flow<List<ItemAlbumEntity>>

    @Query("SELECT * FROM albums WHERE id = :albumId")
    suspend fun getAlbumById(albumId: String): ItemAlbumEntity?
}