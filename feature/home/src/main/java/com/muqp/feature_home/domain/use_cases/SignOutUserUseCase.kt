package com.muqp.feature_home.domain.use_cases

import com.muqp.feature_home.domain.FirebaseAuthRepo
import javax.inject.Inject

class SignOutUserUseCase @Inject constructor(
    private val firebaseAuthRepo: FirebaseAuthRepo
) {
    suspend operator fun invoke() {
        firebaseAuthRepo.signOut()
    }
}