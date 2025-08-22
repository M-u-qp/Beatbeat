package com.muqp.feature_home.domain

interface FirebaseAuthRepo {
    suspend fun signOut()
}