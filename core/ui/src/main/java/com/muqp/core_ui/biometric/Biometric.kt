package com.muqp.core_ui.biometric

import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.AuthenticationCallback
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.muqp.beatbeat.ui.R
import java.lang.ref.WeakReference


class Biometric(
    private val callViewModelMethod: () -> Unit
) {
    fun initializeButtonForBiometric(bButton: Button, fragment: Fragment) {
        val fragmentRef = WeakReference(fragment).get() ?: return
        val context = fragmentRef.requireContext()

        val biometricManager = BiometricManager.from(context)
        val isHaveBiometric: Boolean

        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {

            BiometricManager.BIOMETRIC_SUCCESS -> {
                bButton.visibility = View.VISIBLE
                isHaveBiometric = true
            }

            else -> {
                bButton.visibility = View.GONE
                isHaveBiometric = false
            }
        }

        if (isHaveBiometric) {
            val biometricPrompt = BiometricPrompt(
                fragmentRef,
                ContextCompat.getMainExecutor(context),
                object : AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        callViewModelMethod.invoke()
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.authorization_error),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onAuthenticationFailed() {
                        Toast.makeText(
                            context,
                            context.getString(R.string.authorization_error),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            bButton.setOnClickListener {
                biometricPrompt.authenticate(
                    PromptInfo.Builder()
                        .setTitle(context.getString(R.string.authorization))
                        .setNegativeButtonText(context.getString(R.string.cancel))
                        .build()
                )
            }
        }
    }
}
