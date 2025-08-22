package com.muqp.core_database.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.muqp.core_database.database.entity.ItemTrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(trackEntity: ItemTrackEntity)

    @Delete
    suspend fun deleteTrack(trackEntity: ItemTrackEntity)

    @Query("SELECT * FROM tracks")
    fun getAllTracks(): Flow<List<ItemTrackEntity>>

    @Query("SELECT * FROM tracks WHERE id = :trackId")
    suspend fun getTrackById(trackId: String): ItemTrackEntity?
}