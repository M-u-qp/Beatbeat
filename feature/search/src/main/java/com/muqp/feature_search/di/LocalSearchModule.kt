package com.muqp.feature_search.di

import com.muqp.core_database.database.dao.AlbumDao
import com.muqp.core_database.database.dao.ArtistDao
import com.muqp.core_database.database.dao.PlaylistDao
import com.muqp.core_database.database.dao.PlaylistTracksDao
import com.muqp.core_database.database.dao.TrackDao
import com.muqp.feature_search.data.local.FavoriteRepositoryImpl
import com.muqp.feature_search.data.local.PlaylistRepositoryImpl
import com.muqp.feature_search.domain.local.FavoriteRepository
import com.muqp.feature_search.domain.local.PlaylistRepository
import com.muqp.feature_search.domain.local.use_cases.AddTrackToPlaylistUseCase
import com.muqp.feature_search.domain.local.use_cases.DeleteAlbumUseCase
import com.muqp.feature_search.domain.local.use_cases.DeleteArtistUseCase
import com.muqp.feature_search.domain.local.use_cases.DeleteTrackUseCase
import com.muqp.feature_search.domain.local.use_cases.GetAlbumByIdUseCase
import com.muqp.feature_search.domain.local.use_cases.GetArtistByIdUseCase
import com.muqp.feature_search.domain.local.use_cases.GetPlaylistsUseCase
import com.muqp.feature_search.domain.local.use_cases.GetTrackByIdUseCase
import com.muqp.feature_search.domain.local.use_cases.InsertAlbumUseCase
import com.muqp.feature_search.domain.local.use_cases.InsertArtistUseCase
import com.muqp.feature_search.domain.local.use_cases.InsertTrackUseCase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class LocalSearchModule {
    @Provides
    @Singleton
    fun provideFavoriteRepository(
        trackDao: TrackDao,
        albumDao: AlbumDao,
        artistDao: ArtistDao
    ): FavoriteRepository = FavoriteRepositoryImpl(
        trackDao, albumDao, artistDao
    )

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

    @Provides
    @Singleton
    fun provideGetAlbumByIdUseCase(
        favoriteRepository: FavoriteRepository
    ): GetAlbumByIdUseCase {
        return GetAlbumByIdUseCase(favoriteRepository)
    }

    @Provides
    @Singleton
    fun provideInsertAlbumUseCase(
        favoriteRepository: FavoriteRepository
    ): InsertAlbumUseCase {
        return InsertAlbumUseCase(favoriteRepository)
    }

    @Provides
    @Singleton
    fun provideDeleteAlbumUseCase(
        favoriteRepository: FavoriteRepository
    ): DeleteAlbumUseCase {
        return DeleteAlbumUseCase(favoriteRepository)
    }

    @Provides
    @Singleton
    fun provideGetArtistByIdUseCase(
        favoriteRepository: FavoriteRepository
    ): GetArtistByIdUseCase {
        return GetArtistByIdUseCase(favoriteRepository)
    }

    @Provides
    @Singleton
    fun provideInsertArtistUseCase(
        favoriteRepository: FavoriteRepository
    ): InsertArtistUseCase {
        return InsertArtistUseCase(favoriteRepository)
    }

    @Provides
    @Singleton
    fun provideDeleteArtistUseCase(
        favoriteRepository: FavoriteRepository
    ): DeleteArtistUseCase {
        return DeleteArtistUseCase(favoriteRepository)
    }
}