package com.muqp.core_database.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.muqp.core_database.database.entity.ItemArtistsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArtistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtist(artistEntity: ItemArtistsEntity)

    @Delete
    suspend fun deleteArtist(artistEntity: ItemArtistsEntity)

    @Query("SELECT * FROM artists")
    fun getAllArtists(): Flow<List<ItemArtistsEntity>>

    @Query("SELECT * FROM artists WHERE id = :artistId")
    suspend fun getArtistById(artistId: String): ItemArtistsEntity?
}