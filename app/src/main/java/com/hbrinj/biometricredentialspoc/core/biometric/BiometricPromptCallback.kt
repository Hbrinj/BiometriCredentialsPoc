package com.hbrinj.biometricredentialspoc.core.biometric

import androidx.biometric.BiometricPrompt

class BiometricPromptCallback (
    private val onSuccess: (BiometricPrompt.AuthenticationResult) -> Unit,
    private val onFailure: () -> Unit,
    private val onError: (Int, CharSequence) -> Unit
): BiometricPrompt.AuthenticationCallback() {
    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
        super.onAuthenticationError(errorCode, errString)
        onError(errorCode, errString)
    }

    override fun onAuthenticationFailed() {
        super.onAuthenticationFailed()
        onFailure()
    }

    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
        super.onAuthenticationSucceeded(result)
        onSuccess(result)
    }
}