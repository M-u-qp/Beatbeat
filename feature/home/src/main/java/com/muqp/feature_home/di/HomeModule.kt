package com.muqp.feature_home.di

import com.google.firebase.auth.FirebaseAuth
import com.muqp.core_network.api.JamendoApi
import com.muqp.feature_home.data.FirebaseAuthRepoImpl
import com.muqp.feature_home.data.JamendoRepositoryImpl
import com.muqp.feature_home.domain.FirebaseAuthRepo
import com.muqp.feature_home.domain.JamendoRepository
import com.muqp.feature_home.domain.use_cases.GetMusicFeedsUseCase
import com.muqp.feature_home.domain.use_cases.SignOutUserUseCase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class HomeModule {
    @Provides
    @Singleton
    fun provideJamendoRepository(
        jamendoApi: JamendoApi
    ): JamendoRepository {
        return JamendoRepositoryImpl(jamendoApi)
    }

    @Provides
    @Singleton
    fun provideGetMusicFeedsUseCase(
        jamendoRepository: JamendoRepository
    ): GetMusicFeedsUseCase {
        return GetMusicFeedsUseCase(jamendoRepository)
    }

    @Provides
    @Singleton
    fun provideFirebaseAuthRepo(
        firebaseAuth: FirebaseAuth
    ): FirebaseAuthRepo = FirebaseAuthRepoImpl(firebaseAuth)

    @Provides
    @Singleton
    fun provideSignOutUserUseCase(
        firebaseAuthRepo: FirebaseAuthRepo
    ): SignOutUserUseCase {
        return SignOutUserUseCase(firebaseAuthRepo)
    }
}