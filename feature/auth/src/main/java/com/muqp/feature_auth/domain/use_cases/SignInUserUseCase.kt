package com.muqp.feature_auth.domain.use_cases

import com.muqp.feature_auth.domain.FirebaseAuthRepo
import com.muqp.feature_auth.domain.Result
import javax.inject.Inject

class SignInUserUseCase @Inject constructor(
    private val firebaseAuthRepo: FirebaseAuthRepo
) {
    suspend operator fun invoke(email: String, password: String): Result<Unit> {
        return firebaseAuthRepo.signIn(email, password)
    }
}