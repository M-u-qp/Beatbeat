package com.muqp.feature_favorites.di

import com.muqp.core_database.database.dao.AlbumDao
import com.muqp.core_database.database.dao.ArtistDao
import com.muqp.core_database.database.dao.PlaylistDao
import com.muqp.core_database.database.dao.PlaylistTracksDao
import com.muqp.core_database.database.dao.TrackDao
import com.muqp.feature_favorites.data.FavoriteRepositoryImpl
import com.muqp.feature_favorites.data.PlaylistRepositoryImpl
import com.muqp.feature_favorites.domain.FavoriteRepository
import com.muqp.feature_favorites.domain.PlaylistRepository
import com.muqp.feature_favorites.domain.use_cases.AddTrackToPlaylistUseCase
import com.muqp.feature_favorites.domain.use_cases.ClearPlaylistUseCase
import com.muqp.feature_favorites.domain.use_cases.CreatePlaylistUseCase
import com.muqp.feature_favorites.domain.use_cases.DeleteAlbumUseCase
import com.muqp.feature_favorites.domain.use_cases.DeleteArtistUseCase
import com.muqp.feature_favorites.domain.use_cases.DeletePlaylistUseCase
import com.muqp.feature_favorites.domain.use_cases.DeleteTrackUseCase
import com.muqp.feature_favorites.domain.use_cases.GetAlbumByIdUseCase
import com.muqp.feature_favorites.domain.use_cases.GetAllAlbumsUseCase
import com.muqp.feature_favorites.domain.use_cases.GetAllArtistsUseCase
import com.muqp.feature_favorites.domain.use_cases.GetAllTracksUseCase
import com.muqp.feature_favorites.domain.use_cases.GetArtistByIdUseCase
import com.muqp.feature_favorites.domain.use_cases.GetPlaylistWithTracksUseCase
import com.muqp.feature_favorites.domain.use_cases.GetPlaylistsUseCase
import com.muqp.feature_favorites.domain.use_cases.GetTrackByIdUseCase
import com.muqp.feature_favorites.domain.use_cases.GetTrackCountForPlaylistUseCase
import com.muqp.feature_favorites.domain.use_cases.RemoveTrackFromPlaylistUseCase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class FavoritesModule {
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
        playlistTracksDao: PlaylistTracksDao,
        trackDao: TrackDao
    ): PlaylistRepository = PlaylistRepositoryImpl(
        playlistDao, playlistTracksDao, trackDao
    )

    @Provides
    @Singleton
    fun provideCreatePlaylistUseCase(
        playlistRepository: PlaylistRepository
    ): CreatePlaylistUseCase {
        return CreatePlaylistUseCase(playlistRepository)
    }

    @Provides
    @Singleton
    fun provideDeletePlaylistUseCase(
        playlistRepository: PlaylistRepository
    ): DeletePlaylistUseCase {
        return DeletePlaylistUseCase(playlistRepository)
    }

    @Provides
    @Singleton
    fun provideClearPlaylistUseCase(
        playlistRepository: PlaylistRepository
    ): ClearPlaylistUseCase {
        return ClearPlaylistUseCase(playlistRepository)
    }

    @Provides
    @Singleton
    fun provideGetTrackCountForPlaylistUseCase(
        playlistRepository: PlaylistRepository
    ): GetTrackCountForPlaylistUseCase {
        return GetTrackCountForPlaylistUseCase(playlistRepository)
    }

    @Provides
    @Singleton
    fun provideRemoveTrackFromPlaylistUseCase(
        playlistRepository: PlaylistRepository
    ): RemoveTrackFromPlaylistUseCase {
        return RemoveTrackFromPlaylistUseCase(playlistRepository)
    }

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
    fun provideGetPlaylistWithTracksUseCase(
        playlistRepository: PlaylistRepository
    ): GetPlaylistWithTracksUseCase {
        return GetPlaylistWithTracksUseCase(playlistRepository)
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
    fun provideDeleteTrackUseCase(
        favoriteRepository: FavoriteRepository
    ): DeleteTrackUseCase {
        return DeleteTrackUseCase(favoriteRepository)
    }

    @Provides
    @Singleton
    fun provideGetAllTracksUseCase(
        favoriteRepository: FavoriteRepository
    ): GetAllTracksUseCase {
        return GetAllTracksUseCase(favoriteRepository)
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
    fun provideDeleteAlbumUseCase(
        favoriteRepository: FavoriteRepository
    ): DeleteAlbumUseCase {
        return DeleteAlbumUseCase(favoriteRepository)
    }

    @Provides
    @Singleton
    fun provideGetAllAlbumsUseCase(
        favoriteRepository: FavoriteRepository
    ): GetAllAlbumsUseCase {
        return GetAllAlbumsUseCase(favoriteRepository)
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
    fun provideDeleteArtistUseCase(
        favoriteRepository: FavoriteRepository
    ): DeleteArtistUseCase {
        return DeleteArtistUseCase(favoriteRepository)
    }

    @Provides
    @Singleton
    fun provideGetAllArtistsUseCase(
        favoriteRepository: FavoriteRepository
    ): GetAllArtistsUseCase {
        return GetAllArtistsUseCase(favoriteRepository)
    }
}