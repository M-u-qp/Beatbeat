package com.muqp.feature_listen.di

import com.muqp.core_network.api.JamendoApi
import com.muqp.feature_listen.data.remote.JamendoRepositoryImpl
import com.muqp.feature_listen.domain.remote.JamendoRepository
import com.muqp.feature_listen.domain.remote.use_cases.GetAcousticCollectionUseCase
import com.muqp.feature_listen.domain.remote.use_cases.GetElectricCollectionUseCase
import com.muqp.feature_listen.domain.remote.use_cases.GetFastCollectionUseCase
import com.muqp.feature_listen.domain.remote.use_cases.GetFemaleCollectionUseCase
import com.muqp.feature_listen.domain.remote.use_cases.GetMaleCollectionUseCase
import com.muqp.feature_listen.domain.remote.use_cases.GetSlowCollectionUseCase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RemoteListenModule {
    @Provides
    @Singleton
    fun provideJamendoRepository(
        jamendoApi: JamendoApi
    ): JamendoRepository = JamendoRepositoryImpl(jamendoApi)

    @Provides
    @Singleton
    fun provideGetElectricCollectionUseCase(
        jamendoRepository: JamendoRepository
    ): GetElectricCollectionUseCase {
        return GetElectricCollectionUseCase(jamendoRepository)
    }

    @Provides
    @Singleton
    fun provideGetFemaleCollectionUseCase(
        jamendoRepository: JamendoRepository
    ): GetFemaleCollectionUseCase {
        return GetFemaleCollectionUseCase(jamendoRepository)
    }

    @Provides
    @Singleton
    fun provideGetMaleCollectionUseCase(
        jamendoRepository: JamendoRepository
    ): GetMaleCollectionUseCase {
        return GetMaleCollectionUseCase(jamendoRepository)
    }

    @Provides
    @Singleton
    fun provideGetSlowCollectionUseCase(
        jamendoRepository: JamendoRepository
    ): GetSlowCollectionUseCase {
        return GetSlowCollectionUseCase(jamendoRepository)
    }

    @Provides
    @Singleton
    fun provideGetFastCollectionUseCase(
        jamendoRepository: JamendoRepository
    ): GetFastCollectionUseCase {
        return GetFastCollectionUseCase(jamendoRepository)
    }

    @Provides
    @Singleton
    fun provideGetAcousticCollectionUseCase(
        jamendoRepository: JamendoRepository
    ): GetAcousticCollectionUseCase {
        return GetAcousticCollectionUseCase(jamendoRepository)
    }
}