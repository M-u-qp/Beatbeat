package com.muqp.feature_auth.presentation.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muqp.feature_auth.domain.AuthError
import com.muqp.feature_auth.domain.Result
import com.muqp.feature_auth.domain.use_cases.IsSignedInUserUseCase
import com.muqp.feature_auth.domain.use_cases.RegisterUserUseCase
import com.muqp.feature_auth.domain.use_cases.SendEmailVerificationUseCase
import com.muqp.feature_auth.domain.use_cases.SignInAnonymouslyUseCase
import com.muqp.feature_auth.domain.use_cases.SignInUserUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class AuthViewModel @Inject constructor(
    private val isSignedInUserUseCase: IsSignedInUserUseCase,
    private val registerUserUseCase: RegisterUserUseCase,
    private val signInUserUseCase: SignInUserUseCase,
    private val sendEmailVerificationUseCase: SendEmailVerificationUseCase,
    private val signInAnonymouslyUseCase: SignInAnonymouslyUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<AuthScreenState?>(null)
    val state = _state.asStateFlow()

    private val coroutineEH = CoroutineExceptionHandler { _, throwable ->
        _state.value = AuthScreenState.Error(throwable.message ?: "Unknown error")
    }

    init {
        checkAuthState()
    }

    fun updateErrorMessage(error: AuthError?) {
        _state.update { currentState ->
            when (currentState) {
                is AuthScreenState.SignOut -> currentState.copy(error = error)
                else -> currentState
            }
        }
    }

    fun checkAuthState() {
        viewModelScope.launch(coroutineEH) {
            _state.value = if (isSignedInUserUseCase()) {
                AuthScreenState.SignIn
            } else {
                AuthScreenState.SignOut()
            }
        }
    }

    fun register(email: String, password: String, username: String) {
        viewModelScope.launch(coroutineEH) {
            when (val result = registerUserUseCase(email, password, username)) {
                is Result.Success -> {
                    sendEmailVerificationUseCase()
                    _state.value = AuthScreenState.SignIn
                }

                is Result.Error -> {
                    updateErrorMessage(result.error)
                }
            }
        }
    }

    fun signIn(emailOrLogin: String, password: String) {
        viewModelScope.launch(coroutineEH) {
            when (val result = signInUserUseCase(emailOrLogin, password)) {
                is Result.Success -> {
                    _state.value = AuthScreenState.SignIn
                }

                is Result.Error -> {
                    updateErrorMessage(result.error)
                }
            }
        }
    }

    fun signInAnonymously() {
        viewModelScope.launch(coroutineEH) {
            when (val result = signInAnonymouslyUseCase.invoke()) {
                is Result.Success -> {
                    _state.value = AuthScreenState.SignIn
                }

                is Result.Error -> {
                    updateErrorMessage(result.error)
                }
            }
        }
    }
}