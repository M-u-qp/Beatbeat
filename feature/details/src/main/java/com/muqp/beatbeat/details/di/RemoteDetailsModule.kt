package com.muqp.beatbeat.details.di

import com.muqp.beatbeat.details.data.remote.JamendoRepositoryImpl
import com.muqp.beatbeat.details.domain.remote.JamendoRepository
import com.muqp.beatbeat.details.domain.remote.use_cases.GetAlbumDetailsUseCase
import com.muqp.beatbeat.details.domain.remote.use_cases.GetAllArtistAlbumsUseCase
import com.muqp.beatbeat.details.domain.remote.use_cases.GetPagingPopularArtistTracksUseCase
import com.muqp.beatbeat.details.domain.remote.use_cases.GetPopularArtistTracksUseCase
import com.muqp.core_network.api.JamendoApi
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RemoteDetailsModule {
    @Provides
    @Singleton
    fun provideJamendoRepository(
        jamendoApi: JamendoApi
    ): JamendoRepository =
        JamendoRepositoryImpl(jamendoApi)

    @Provides
    @Singleton
    fun provideGetAlbumDetailsUseCase(
        jamendoRepository: JamendoRepository
    ): GetAlbumDetailsUseCase {
        return GetAlbumDetailsUseCase(jamendoRepository)
    }

    @Provides
    @Singleton
    fun provideGetAllArtistAlbumsUseCase(
        jamendoRepository: JamendoRepository
    ): GetAllArtistAlbumsUseCase {
        return GetAllArtistAlbumsUseCase(jamendoRepository)
    }

    @Provides
    @Singleton
    fun provideGetPopularArtistTracksUseCase(
        jamendoRepository: JamendoRepository
    ): GetPopularArtistTracksUseCase {
        return GetPopularArtistTracksUseCase(jamendoRepository)
    }

    @Provides
    @Singleton
    fun provideGetPagingPopularArtistTracksUseCase(
        jamendoRepository: JamendoRepository
    ): GetPagingPopularArtistTracksUseCase {
        return GetPagingPopularArtistTracksUseCase(jamendoRepository)
    }
}