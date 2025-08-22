package com.muqp.feature_home.data

import com.google.firebase.auth.FirebaseAuth
import com.muqp.feature_home.domain.FirebaseAuthRepo
import javax.inject.Inject

class FirebaseAuthRepoImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
): FirebaseAuthRepo {
    override suspend fun signOut() {
        firebaseAuth.signOut()
    }
}