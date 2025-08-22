package com.muqp.feature_auth.domain

import androidx.annotation.StringRes

sealed class AuthError {
    data class Message(val message: String): AuthError()
    data class Resource(@StringRes val resId: Int): AuthError()

    data object UsernameContainsAt : AuthError()
    data object UsernameEmpty : AuthError()
    data object UsernameLengthInvalid : AuthError()
    data object UsernameInvalidChars : AuthError()
    data object UsernameTaken : AuthError()
    data object UserNotFound : AuthError()
    data object EmailNotFound : AuthError()
    data object UserNotAuthenticated : AuthError()

    data object WeakPassword : AuthError()
    data object InvalidCredentials : AuthError()
    data object UserCollision : AuthError()
    data object InvalidUser : AuthError()
    data object NetworkError : AuthError()
    data object UnknownError : AuthError()
    data object InvalidEmail : AuthError()
}

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val error: AuthError) : Result<Nothing>()
}