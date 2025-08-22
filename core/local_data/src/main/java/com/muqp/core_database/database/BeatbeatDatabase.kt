package com.muqp.core_database.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.muqp.core_database.database.dao.AlbumDao
import com.muqp.core_database.database.dao.ArtistDao
import com.muqp.core_database.database.dao.PlaylistDao
import com.muqp.core_database.database.dao.PlaylistTracksDao
import com.muqp.core_database.database.dao.TrackDao
import com.muqp.core_database.database.entity.ItemAlbumEntity
import com.muqp.core_database.database.entity.ItemArtistsEntity
import com.muqp.core_database.database.entity.ItemTrackEntity
import com.muqp.core_database.database.entity.PlaylistEntity
import com.muqp.core_database.database.entity.PlaylistTrackCrossRef

@Database(
    entities = [
        ItemTrackEntity::class,
        ItemAlbumEntity::class,
        ItemArtistsEntity::class,
        PlaylistEntity::class,
        PlaylistTrackCrossRef::class
    ],
    version = 2,
    exportSchema = false
)
abstract class BeatbeatDatabase : RoomDatabase() {
    companion object {
        const val BEATBEAT_DB_NAME = "beatbeatDBName"
    }

    abstract val trackDao: TrackDao
    abstract val albumDao: AlbumDao
    abstract val artistDao: ArtistDao
    abstract val playlistDao: PlaylistDao
    abstract val playlistTracksDao: PlaylistTracksDao
}