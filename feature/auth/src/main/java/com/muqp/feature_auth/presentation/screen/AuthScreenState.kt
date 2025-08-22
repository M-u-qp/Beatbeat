package com.muqp.feature_auth.presentation.screen

import com.muqp.feature_auth.domain.AuthError

sealed class AuthScreenState {
    data object SignIn : AuthScreenState()
    data class SignOut(var error: AuthError? = null) : AuthScreenState()
    data class Error(val message: String) : AuthScreenState()
}