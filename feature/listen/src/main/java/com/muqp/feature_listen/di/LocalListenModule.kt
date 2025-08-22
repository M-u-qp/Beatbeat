package com.muqp.feature_listen.di

import com.muqp.core_database.database.dao.PlaylistDao
import com.muqp.core_database.database.dao.PlaylistTracksDao
import com.muqp.core_database.database.dao.TrackDao
import com.muqp.feature_listen.data.local.FavoriteRepositoryImpl
import com.muqp.feature_listen.data.local.PlaylistRepositoryImpl
import com.muqp.feature_listen.domain.local.FavoriteRepository
import com.muqp.feature_listen.domain.local.PlaylistRepository
import com.muqp.feature_listen.domain.local.use_cases.AddTrackToPlaylistUseCase
import com.muqp.feature_listen.domain.local.use_cases.DeleteTrackUseCase
import com.muqp.feature_listen.domain.local.use_cases.GetPlaylistsUseCase
import com.muqp.feature_listen.domain.local.use_cases.GetTrackByIdUseCase
import com.muqp.feature_listen.domain.local.use_cases.InsertTrackUseCase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class LocalListenModule {
    @Provides
    @Singleton
    fun provideFavoriteRepository(
        trackDao: TrackDao
    ): FavoriteRepository = FavoriteRepositoryImpl(trackDao)

    @Provides
    @Singleton
    fun providePlaylistRepository(
        playlistDao: PlaylistDao,
        playlistTracksDao: PlaylistTracksDao
    ): PlaylistRepository = PlaylistRepositoryImpl(
        playlistDao, playlistTracksDao
    )

    @Provides
    @Singleton
    fun provideGetPlaylistsUseCase(
        playlistRepository: PlaylistRepository
    ): GetPlaylistsUseCase {
        return GetPlaylistsUseCase(playlistRepository)
    }

    @Provides
    @Singleton
    fun provideAddTrackToPlaylistUseCase(
        playlistRepository: PlaylistRepository
    ): AddTrackToPlaylistUseCase {
        return AddTrackToPlaylistUseCase(playlistRepository)
    }

    @Provides
    @Singleton
    fun provideGetTrackByIdUseCase(
        favoriteRepository: FavoriteRepository
    ): GetTrackByIdUseCase {
        return GetTrackByIdUseCase(favoriteRepository)
    }

    @Provides
    @Singleton
    fun provideInsertTrackUseCase(
        favoriteRepository: FavoriteRepository
    ): InsertTrackUseCase {
        return InsertTrackUseCase(favoriteRepository)
    }

    @Provides
    @Singleton
    fun provideDeleteTrackUseCase(
        favoriteRepository: FavoriteRepository
    ): DeleteTrackUseCase {
        return DeleteTrackUseCase(favoriteRepository)
    }
}