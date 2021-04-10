package com.hbrinj.biometricredentialspoc.core.biometric

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED
import androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS
import javax.inject.Inject

class BiometricsController @Inject constructor(private val biometricManager: BiometricManager) {
    val enrollmentState: EnrollmentState
        get() = determineEnrollmentState()

    val allowedAuth = DEVICE_CREDENTIAL or BiometricManager.Authenticators.BIOMETRIC_WEAK

    private fun determineEnrollmentState(): EnrollmentState {
        return when(biometricManager.canAuthenticate(allowedAuth)) {
            BIOMETRIC_SUCCESS -> EnrollmentState.Enrolled
            BIOMETRIC_ERROR_NONE_ENROLLED -> EnrollmentState.CanEnroll
            else -> EnrollmentState.NotAvailable
        }
    }
}

sealed class EnrollmentState {
    object Enrolled : EnrollmentState()
    object NotAvailable : EnrollmentState()
    object CanEnroll : EnrollmentState()
}