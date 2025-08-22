package com.muqp.core_database.di

import android.app.Application
import androidx.room.Room
import com.muqp.core_database.database.BeatbeatDatabase
import com.muqp.core_database.database.BeatbeatDatabase.Companion.BEATBEAT_DB_NAME
import com.muqp.core_database.database.dao.AlbumDao
import com.muqp.core_database.database.dao.ArtistDao
import com.muqp.core_database.database.dao.PlaylistDao
import com.muqp.core_database.database.dao.PlaylistTracksDao
import com.muqp.core_database.database.dao.TrackDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {
    @Provides
    @Singleton
    fun provideBeatbeatDatabase(
        application: Application
    ): BeatbeatDatabase {
        return Room.databaseBuilder(
            context = application,
            klass = BeatbeatDatabase::class.java,
            name = BEATBEAT_DB_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideTrackDao(
        beatbeatDatabase: BeatbeatDatabase
    ): TrackDao = beatbeatDatabase.trackDao

    @Provides
    @Singleton
    fun provideAlbumDao(
        beatbeatDatabase: BeatbeatDatabase
    ): AlbumDao = beatbeatDatabase.albumDao

    @Provides
    @Singleton
    fun provideArtistDao(
        beatbeatDatabase: BeatbeatDatabase
    ): ArtistDao = beatbeatDatabase.artistDao

    @Provides
    @Singleton
    fun providePlaylistDao(
        beatbeatDatabase: BeatbeatDatabase
    ): PlaylistDao = beatbeatDatabase.playlistDao

    @Provides
    @Singleton
    fun providePlaylistTracksDao(
        beatbeatDatabase: BeatbeatDatabase
    ): PlaylistTracksDao = beatbeatDatabase.playlistTracksDao
}

