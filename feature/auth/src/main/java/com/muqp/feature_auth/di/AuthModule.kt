package com.muqp.feature_auth.di

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.BuildConfig
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.muqp.feature_auth.data.AuthAnonPrefs
import com.muqp.feature_auth.data.FirebaseAuthRepoImpl
import com.muqp.feature_auth.domain.FirebaseAuthRepo
import com.muqp.feature_auth.domain.use_cases.IsSignedInUserUseCase
import com.muqp.feature_auth.domain.use_cases.RegisterUserUseCase
import com.muqp.feature_auth.domain.use_cases.SendEmailVerificationUseCase
import com.muqp.feature_auth.domain.use_cases.SignInAnonymouslyUseCase
import com.muqp.feature_auth.domain.use_cases.SignInUserUseCase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AuthModule {
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = Firebase.auth

    @Provides
    @Singleton
    fun provideFirebaseUser(auth: FirebaseAuth): FirebaseUser? = auth.currentUser

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        val db = FirebaseFirestore.getInstance()
        FirebaseFirestore.setLoggingEnabled(BuildConfig.DEBUG)
        return db
    }

    @Provides
    @Singleton
    fun provideAuthAnonPrefs(application: Application): AuthAnonPrefs = AuthAnonPrefs(application)

    @Provides
    @Singleton
    fun provideFirebaseAuthRepo(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore,
        authAnonPrefs: AuthAnonPrefs
    ): FirebaseAuthRepo = FirebaseAuthRepoImpl(firebaseAuth, firestore, authAnonPrefs)

    @Provides
    @Singleton
    fun provideIsSignedInUserUseCase(firebaseAuthRepo: FirebaseAuthRepo): IsSignedInUserUseCase {
        return IsSignedInUserUseCase(firebaseAuthRepo)
    }

    @Provides
    @Singleton
    fun provideRegisterUserUseCase(firebaseAuthRepo: FirebaseAuthRepo): RegisterUserUseCase {
        return RegisterUserUseCase(firebaseAuthRepo)
    }

    @Provides
    @Singleton
    fun provideSendEmailVerificationUseCase(firebaseAuthRepo: FirebaseAuthRepo): SendEmailVerificationUseCase {
        return SendEmailVerificationUseCase(firebaseAuthRepo)
    }

    @Provides
    @Singleton
    fun provideSignInUserUseCase(firebaseAuthRepo: FirebaseAuthRepo): SignInUserUseCase {
        return SignInUserUseCase(firebaseAuthRepo)
    }

    @Provides
    @Singleton
    fun provideSignInAnonymouslyUseCase(firebaseAuthRepo: FirebaseAuthRepo): SignInAnonymouslyUseCase {
        return SignInAnonymouslyUseCase(firebaseAuthRepo)
    }
}