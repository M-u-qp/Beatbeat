package com.muqp.feature_auth.presentation.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.muqp.beatbeat.auth.R
import com.muqp.beatbeat.auth.databinding.FragmentAuthorizationBinding
import com.muqp.core_ui.biometric.Biometric
import com.muqp.core_utils.extensions.NavigationExt.navigate
import com.muqp.core_utils.has_dependencies.HasDependencies
import com.muqp.feature_auth.common.RegisterDialog
import com.muqp.feature_auth.model.UserUI
import com.muqp.feature_auth.presentation.screen.GetAuthErrorMessage.getErrorMessage
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.muqp.beatbeat.ui.R as CoreUi

class AuthorizationFragment : Fragment() {

    private var _binding: FragmentAuthorizationBinding? = null
    private val binding get() = _binding!!

    private lateinit var biometric: Biometric
    private lateinit var registerDialog: RegisterDialog

    private lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: AuthViewModel by viewModels { viewModelFactory }
    private val user = UserUI()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthorizationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dependencies = (requireActivity() as HasDependencies).getViewModelFactory()
        viewModelFactory = dependencies.provideViewModelFactory()
        initListeners()
    }

    private fun initListeners() = with(binding) {
        val cannotBeEmpty = requireContext().getString(CoreUi.string.fields_cannot_be_empty)
        registerDialog = RegisterDialog(requireContext())
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collectLatest { state ->
                when (state) {
                    is AuthScreenState.SignIn -> {
                        navigate(
                            actionId = R.id.action_authFragment_to_mainFragment
                        )
                    }

                    is AuthScreenState.SignOut -> {
                        tvRegister.text = requireContext().getString(CoreUi.string.register)
                        bSignIn.setOnClickListener {
                            if (etLogin.text.isNotEmpty() && etPassword.text.isNotEmpty()) {
                                user.email = etLogin.text.toString()
                                user.password = etPassword.text.toString()
                                viewModel.signIn(user.email, user.password)
                            } else {
                                Toast.makeText(requireContext(), cannotBeEmpty, Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                        tvRegister.setOnClickListener {
                            registerDialog.show(
                                title = requireContext().getString(CoreUi.string.registration),
                                onRegister = { username, email, password ->
                                    user.email = email
                                    user.password = password
                                    user.username = username
                                    viewModel.register(
                                    email = user.email,
                                    password = user.password,
                                    username = user.username
                                    )
                                }
                            )
                        }
                        biometric = Biometric(
                            callViewModelMethod = {
                                viewModel.signInAnonymously()
                            }
                        )

                        biometric.initializeButtonForBiometric(
                            bButton = bBiometric,
                            fragment = this@AuthorizationFragment
                        )

                        state.error?.let { error ->
                            val errorMessage = getErrorMessage(error, requireContext())
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
                            viewModel.updateErrorMessage(null)
                        }
                    }
                    is AuthScreenState.Error -> {
                        Toast.makeText(
                            requireContext(),
                            state.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    null -> {}
                }
            }
        }
    }
}