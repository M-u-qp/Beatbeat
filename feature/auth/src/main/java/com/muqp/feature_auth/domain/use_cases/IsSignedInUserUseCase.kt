package com.muqp.feature_auth.domain.use_cases

import com.muqp.feature_auth.domain.FirebaseAuthRepo
import javax.inject.Inject

class IsSignedInUserUseCase @Inject constructor(
    private val firebaseAuthRepo: FirebaseAuthRepo
) {
    suspend operator fun invoke(): Boolean {
        return firebaseAuthRepo.isUserSignedIn()
    }
}