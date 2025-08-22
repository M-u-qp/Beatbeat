package com.muqp.feature_auth.domain

import com.google.firebase.auth.FirebaseUser

interface FirebaseAuthRepo {
    suspend fun registerNewUser(
        email: String,
        password: String,
        username: String
    ): Result<Unit>

    suspend fun signIn(
        emailOrUsername: String,
        password: String
    ): Result<Unit>

    suspend fun signInAnonymously(): Result<Unit>

    suspend fun isUserSignedIn(): Boolean

    suspend fun sendEmailVerification(): Result<Unit>

    fun getCurrentUser(): FirebaseUser?

    suspend fun getUsername(uid: String): Result<String>
}