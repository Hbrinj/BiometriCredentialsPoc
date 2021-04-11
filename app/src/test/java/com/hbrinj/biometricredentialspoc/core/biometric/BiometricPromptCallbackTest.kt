package com.hbrinj.biometricredentialspoc.core.biometric

import androidx.biometric.BiometricPrompt
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

internal class BiometricPromptCallbackTest {

    private val mockOnError: (Int, CharSequence) -> Unit = mock()
    private val mockOnFailure: () -> Unit = mock()
    private val mockOnSuccess: (BiometricPrompt.AuthenticationResult) -> Unit = mock()

    private lateinit var sut: BiometricPromptCallback

    @BeforeEach
    fun setup() {
        sut = BiometricPromptCallback(mockOnSuccess, mockOnFailure, mockOnError)
    }

    @Test
    fun `onError called when the auth errors`() {
        // Given

        // When
        sut.onAuthenticationError(1, "hello")

        // Then
        verify(mockOnError).invoke(eq(1), eq("hello"))
    }

    @Test
    fun `onFailure called when the auth fails `() {
        // Given

        // When
        sut.onAuthenticationFailed()

        // Then
        verify(mockOnFailure).invoke()
    }

    @Test
    fun `onSuccess called when the auth succeeds `() {
        // Given
        val mockResult: BiometricPrompt.AuthenticationResult = mock()

        // When
        sut.onAuthenticationSucceeded(mockResult)

        // Then
        verify(mockOnSuccess).invoke(eq(mockResult))
    }
}