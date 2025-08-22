package com.muqp.feature_auth

import com.muqp.feature_auth.domain.AuthError
import com.muqp.feature_auth.domain.Result
import com.muqp.feature_auth.domain.use_cases.IsSignedInUserUseCase
import com.muqp.feature_auth.domain.use_cases.RegisterUserUseCase
import com.muqp.feature_auth.domain.use_cases.SendEmailVerificationUseCase
import com.muqp.feature_auth.domain.use_cases.SignInAnonymouslyUseCase
import com.muqp.feature_auth.domain.use_cases.SignInUserUseCase
import com.muqp.feature_auth.presentation.screen.AuthScreenState
import com.muqp.feature_auth.presentation.screen.AuthViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.Before
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {
    private lateinit var isSignedInUserUseCase: IsSignedInUserUseCase
    private lateinit var registerUserUseCase: RegisterUserUseCase
    private lateinit var signInUserUseCase: SignInUserUseCase
    private lateinit var signInAnonymouslyUseCase: SignInAnonymouslyUseCase
    private lateinit var sendEmailVerificationUseCase: SendEmailVerificationUseCase

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        isSignedInUserUseCase = mock()
        registerUserUseCase = mock()
        signInUserUseCase = mock()
        signInAnonymouslyUseCase = mock()
        sendEmailVerificationUseCase = mock()

        Dispatchers.setMain(testDispatcher)

        viewModel = AuthViewModel(
            isSignedInUserUseCase,
            registerUserUseCase,
            signInUserUseCase,
            sendEmailVerificationUseCase,
            signInAnonymouslyUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // Тест 1: Проверка состояния аутентификации (пользователь вошел)
    @Test
    fun `checkAuthState should set SignIn state when user is signed in`() = runTest {
        `when`(isSignedInUserUseCase()).thenReturn(true)
        viewModel.checkAuthState()
        assert(viewModel.state.value is AuthScreenState.SignIn)
    }

    // Тест 2: Успешная регистрация
    @Test
    fun `register should set SignIn state on success`() = runTest {
        `when`(registerUserUseCase("test@mail.ru", "123", "user"))
            .thenReturn(Result.Success(Unit))
        viewModel.register("test@mail.ru", "123", "user")
        assert(viewModel.state.value is AuthScreenState.SignIn)
        verify(sendEmailVerificationUseCase).invoke()
    }

    // Тест 3: Ошибка при входе
    @Test
    fun `signIn should update error state on failure`() = runTest {
        `when`(isSignedInUserUseCase()).thenReturn(false)
        viewModel.checkAuthState()
        val error = AuthError.NetworkError
        `when`(signInUserUseCase("user", "wrong_pass"))
            .thenReturn(Result.Error(error))
        viewModel.signIn("user", "wrong_pass")
        val currentState = viewModel.state.value
        assertTrue(currentState is AuthScreenState.SignOut)
        assertEquals(error, (currentState as AuthScreenState.SignOut).error)
    }

    // Тест 4: Анонимный вход
    @Test
    fun `signInAnonymously should set SignIn state on success`() = runTest {
        `when`(signInAnonymouslyUseCase.invoke())
            .thenReturn(Result.Success(Unit))

        viewModel.signInAnonymously()

        assert(viewModel.state.value is AuthScreenState.SignIn)
    }
}