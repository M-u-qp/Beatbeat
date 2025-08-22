package com.muqp.feature_auth.data

import android.util.Patterns
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.muqp.feature_auth.domain.AuthError
import com.muqp.feature_auth.domain.FirebaseAuthRepo
import com.muqp.feature_auth.domain.Result
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthRepoImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val authAnonPrefs: AuthAnonPrefs
) : FirebaseAuthRepo {

    companion object {
        private const val USERS_COLLECTION = "users"
        private const val USERNAME_FIELD = "username"
        private const val EMAIL_FIELD = "email"
        private val USERNAME_REGEX = "^[a-zA-Z0-9_.-]+$".toRegex()
        private const val MIN_USERNAME_LENGTH = 3
        private const val MAX_USERNAME_LENGTH = 20
    }

    override suspend fun registerNewUser(
        email: String,
        password: String,
        username: String
    ): Result<Unit> {
        return try {
            when {
                username.contains("@") -> return Result.Error(AuthError.UsernameContainsAt)
                username.isBlank() -> return Result.Error(AuthError.UsernameEmpty)
                username.length < MIN_USERNAME_LENGTH || username.length > MAX_USERNAME_LENGTH ->
                    return Result.Error(AuthError.UsernameLengthInvalid)
                !username.matches(USERNAME_REGEX) -> return Result.Error(AuthError.UsernameInvalidChars)
            }

            val normalizedUsername = username.lowercase()

            val usernameQuery =
                try {
                    firestore.collection(USERS_COLLECTION)
                        .whereEqualTo(USERNAME_FIELD, normalizedUsername)
                        .get()
                        .await()
                } catch (e: Exception) {
                    return Result.Error(getFirebaseErrorMessage(e))
                }

            if (!usernameQuery.isEmpty) {
                return Result.Error(AuthError.UsernameTaken)
            }

            val normalizedEmail = email.trim().lowercase()
            if (!isEmailValid(normalizedEmail)) {
                return Result.Error(AuthError.InvalidEmail)
            }

            val authResult = try {
                firebaseAuth.createUserWithEmailAndPassword(normalizedEmail, password).await()
            } catch (e: Exception) {
                return Result.Error(getFirebaseErrorMessage(e))
            }

            val userData = mapOf(
                USERNAME_FIELD to normalizedUsername,
                EMAIL_FIELD to normalizedEmail
            )

            try {
                firestore.collection(USERS_COLLECTION)
                    .document(authResult.user?.uid ?: "")
                    .set(userData)
                    .await()
            } catch (e: Exception) {
                authResult.user?.delete()?.await()
                return Result.Error(getFirebaseErrorMessage(e))
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(getFirebaseErrorMessage(e))
        }
    }

    override suspend fun signIn(emailOrUsername: String, password: String): Result<Unit> {
        return try {
            val normalizedEmailOrUsername = emailOrUsername.lowercase()
            if (isEmailValid(normalizedEmailOrUsername)) {
                firebaseAuth.signInWithEmailAndPassword(normalizedEmailOrUsername, password).await()
            } else {
                val usernameQuery = firestore.collection(USERS_COLLECTION)
                    .whereEqualTo(USERNAME_FIELD, normalizedEmailOrUsername)
                    .get()
                    .await()

                if (usernameQuery.isEmpty) {
                    return Result.Error(AuthError.UserNotFound)
                }

                val email = usernameQuery.documents[0].getString(EMAIL_FIELD)
                    ?: return Result.Error(AuthError.EmailNotFound)

                firebaseAuth.signInWithEmailAndPassword(email, password).await()
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(getFirebaseErrorMessage(e))
        }
    }

    override suspend fun signInAnonymously(): Result<Unit> {
        return try {
            val savedUid = authAnonPrefs.getAnonUserUid()
            val currentUser = firebaseAuth.currentUser

            if (savedUid != null && currentUser != null && currentUser.uid == savedUid) {
                return Result.Success(Unit)
            }

            if (savedUid != null && currentUser == null) {
                try {
                    firebaseAuth.signInAnonymously().await()
                    return Result.Success(Unit)
                } catch (e: Exception) {
                    Result.Error(getFirebaseErrorMessage(e))
                }
            }

            val authResult = firebaseAuth.signInAnonymously().await()
            authResult.user?.let { user ->
                authAnonPrefs.saveAnonUserUid(user.uid)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(getFirebaseErrorMessage(e))
        }
    }

    override suspend fun isUserSignedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    override suspend fun sendEmailVerification(): Result<Unit> {
        return try {
            firebaseAuth.currentUser?.sendEmailVerification()?.await()
                ?: return Result.Error(AuthError.UserNotAuthenticated)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(getFirebaseErrorMessage(e))
        }
    }

    override fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    override suspend fun getUsername(uid: String): Result<String> {
        return try {
            val document = firestore.collection(USERS_COLLECTION)
                .document(uid)
                .get()
                .await()

            val username = document.getString(USERNAME_FIELD)
                ?: return Result.Error(AuthError.UserNotFound)
            Result.Success(username)
        } catch (e: Exception) {
            Result.Error(getFirebaseErrorMessage(e))
        }
    }

    private fun isEmailValid(email: String): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

    private fun getFirebaseErrorMessage(e: Exception): AuthError {
        return when (e) {
            is FirebaseAuthWeakPasswordException -> AuthError.WeakPassword
            is FirebaseAuthInvalidCredentialsException -> AuthError.InvalidCredentials
            is FirebaseAuthUserCollisionException -> AuthError.UserCollision
            is FirebaseAuthInvalidUserException -> AuthError.InvalidUser
            is FirebaseNetworkException -> AuthError.NetworkError
            else -> AuthError.UnknownError
        }
    }
}