package com.muqp.feature_search.di

import com.muqp.core_network.api.JamendoApi
import com.muqp.feature_search.data.remote.JamendoRepositoryImpl
import com.muqp.feature_search.domain.remote.JamendoRepository
import com.muqp.feature_search.domain.remote.use_cases.GetSearchResultToAlbumUseCase
import com.muqp.feature_search.domain.remote.use_cases.GetSearchResultToArtistUseCase
import com.muqp.feature_search.domain.remote.use_cases.GetSearchResultToTrackUseCase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RemoteSearchModule {
    @Provides
    @Singleton
    fun provideJamendoRepository(
        jamendoApi: JamendoApi
    ): JamendoRepository =
        JamendoRepositoryImpl(jamendoApi)

    @Provides
    @Singleton
    fun provideGetSearchResultToTrackUseCase(
        jamendoRepository: JamendoRepository
    ): GetSearchResultToTrackUseCase {
        return GetSearchResultToTrackUseCase(
            jamendoRepository
        )
    }

    @Provides
    @Singleton
    fun provideGetSearchResultToAlbumUseCase(
        jamendoRepository: JamendoRepository
    ): GetSearchResultToAlbumUseCase {
        return GetSearchResultToAlbumUseCase(
            jamendoRepository
        )
    }

    @Provides
    @Singleton
    fun provideGetSearchResultToArtistUseCase(
        jamendoRepository: JamendoRepository
    ): GetSearchResultToArtistUseCase {
        return GetSearchResultToArtistUseCase(
            jamendoRepository
        )
    }
}