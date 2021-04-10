package com.hbrinj.biometricredentialspoc.core.biometric

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE
import androidx.biometric.BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED
import androidx.biometric.BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE
import androidx.biometric.BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED
import androidx.biometric.BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED
import androidx.biometric.BiometricManager.BIOMETRIC_STATUS_UNKNOWN
import androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

internal class BiometricsControllerTest {
    private lateinit var biometricManager: BiometricManager

    private lateinit var sut: BiometricsController

    @BeforeEach
    fun setup() {
        biometricManager = mock{
            on { canAuthenticate(any()) } doReturn BIOMETRIC_SUCCESS
        }

        sut = BiometricsController(biometricManager)
    }

    @Test
    fun `When biometric or device auth found but not enrolled return CanEnroll`() {
        // Given
        whenever(biometricManager.canAuthenticate(any()))
            .thenReturn(BIOMETRIC_ERROR_NONE_ENROLLED)

        // When
        val result = sut.enrollmentState

        // Then
        assertEquals(EnrollmentState.CanEnroll, result)
        verify(biometricManager).canAuthenticate(eq(DEVICE_CREDENTIAL or BIOMETRIC_WEAK))
    }

    @Test
    fun `When biometric or device auth found and enrolled return`() {
        // Given

        // When
        val result = sut.enrollmentState

        // Then
        assertEquals(EnrollmentState.Enrolled, result)
        verify(biometricManager).canAuthenticate(eq(DEVICE_CREDENTIAL or BIOMETRIC_WEAK))
    }

    @ParameterizedTest
    @ValueSource(ints = [
        BIOMETRIC_ERROR_HW_UNAVAILABLE,
        BIOMETRIC_ERROR_NO_HARDWARE,
        BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED,
        BIOMETRIC_ERROR_UNSUPPORTED,
        BIOMETRIC_STATUS_UNKNOWN
    ])
    fun `When any other states emitted from Biometrics Manager return NotAvailable`(biometricState: Int) {
        // Given
        whenever(biometricManager.canAuthenticate(any()))
            .thenReturn(biometricState)

        // When
        val result = sut.enrollmentState

        // Then
        assertEquals(EnrollmentState.NotAvailable, result)
        verify(biometricManager).canAuthenticate(eq(DEVICE_CREDENTIAL or BIOMETRIC_WEAK))
    }

    @Test
    fun `When calling allowed Auth it return the logical or of Device credential and Biometric Weak`() {
        // Given

        // When
        val result = sut.allowedAuth

        // Then
        assertEquals(DEVICE_CREDENTIAL or BIOMETRIC_WEAK, result)
    }
}
