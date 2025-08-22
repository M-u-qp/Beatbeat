package com.muqp.feature_auth.presentation.screen

import android.content.Context
import com.muqp.beatbeat.ui.R
import com.muqp.feature_auth.domain.AuthError
import java.lang.ref.WeakReference

object GetAuthErrorMessage {
    fun getErrorMessage(authError: AuthError, context: Context): String {
        val contextRef = WeakReference(context)
        return when (authError) {
            AuthError.UsernameContainsAt -> contextRef.get()
                ?.getString(R.string.error_username_contains_at) ?: ""

            AuthError.UsernameEmpty -> contextRef.get()?.getString(R.string.error_username_empty)
                ?: ""

            AuthError.UsernameLengthInvalid -> contextRef.get()
                ?.getString(R.string.error_username_length) ?: ""

            AuthError.UsernameInvalidChars -> contextRef.get()
                ?.getString(R.string.error_username_invalid_chars) ?: ""

            AuthError.UsernameTaken -> contextRef.get()?.getString(R.string.error_username_taken)
                ?: ""

            AuthError.UserNotFound -> contextRef.get()?.getString(R.string.error_user_not_found)
                ?: ""

            AuthError.EmailNotFound -> contextRef.get()?.getString(R.string.error_email_not_found)
                ?: ""

            AuthError.UserNotAuthenticated -> contextRef.get()
                ?.getString(R.string.error_user_not_authenticated) ?: ""

            AuthError.WeakPassword -> contextRef.get()?.getString(R.string.error_weak_password)
                ?: ""

            AuthError.InvalidCredentials -> contextRef.get()
                ?.getString(R.string.error_invalid_credentials) ?: ""

            AuthError.UserCollision -> contextRef.get()?.getString(R.string.error_user_collision)
                ?: ""

            AuthError.InvalidUser -> contextRef.get()?.getString(R.string.error_invalid_user) ?: ""
            AuthError.InvalidEmail -> contextRef.get()?.getString(R.string.error_invalid_email)
                ?: ""

            AuthError.NetworkError -> contextRef.get()?.getString(R.string.error_network) ?: ""
            is AuthError.Message -> authError.message
            is AuthError.Resource -> try {
                contextRef.get()?.getString(authError.resId) ?: ""
            } catch (e: Exception) {
                contextRef.get()?.getString(R.string.error_unknown) ?: ""
            }

            else -> contextRef.get()?.getString(R.string.error_unknown) ?: ""
        }
    }
}