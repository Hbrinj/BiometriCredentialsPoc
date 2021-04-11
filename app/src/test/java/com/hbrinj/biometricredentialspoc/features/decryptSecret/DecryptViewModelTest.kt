package com.hbrinj.biometricredentialspoc.features.decryptSecret

import androidx.biometric.BiometricPrompt
import androidx.lifecycle.Observer
import com.hbrinj.biometricredentialspoc.InstantTaskExecutionExtension
import com.hbrinj.biometricredentialspoc.core.architecture.DataEvent
import com.hbrinj.biometricredentialspoc.core.biometric.BiometricSharedPreferencesController
import com.hbrinj.biometricredentialspoc.core.biometric.BiometricsController
import com.hbrinj.biometricredentialspoc.core.biometric.EnrollmentState
import com.hbrinj.biometricredentialspoc.core.crypto.EncryptedMessage
import com.hbrinj.biometricredentialspoc.core.crypto.SecretController
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.KArgumentCaptor
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyBlocking
import org.mockito.kotlin.verifyZeroInteractions
import org.mockito.kotlin.whenever

@ExtendWith(InstantTaskExecutionExtension::class)
internal class DecryptViewModelTest {

    private val mockEncryptedMessage: EncryptedMessage = mock()
    private val mockDecryptedData = "wibble".toByteArray()
    private val mockAuthResult: BiometricPrompt.AuthenticationResult = mock()
    private val mockBiometricsController: BiometricsController = mock {
        on {enrollmentState} doReturn EnrollmentState.Enrolled
    }
    private val mockSecretController: SecretController = mock {
        onBlocking { decryptData(any()) } doReturn mockDecryptedData
    }
    private val mockBiometricSharedPreferencesController: BiometricSharedPreferencesController = mock {
        onBlocking { retrieveEncryptedMessage(any()) } doReturn mockEncryptedMessage
    }

    private val decryptedTextEventObserver: Observer<DataEvent<String>> = mock()
    private val decryptedTextEventCaptor: KArgumentCaptor<DataEvent<String>> = argumentCaptor()

    private lateinit var sut: DecryptViewModel

    @BeforeEach
    fun setup() {
        sut = DecryptViewModel(
            mockBiometricsController,
            mockSecretController,
            mockBiometricSharedPreferencesController
        )
        sut.decryptedTextEvent.observeForever(decryptedTextEventObserver)
    }

    @Test
    fun `Correctly use the key name to decrypt the secret from Shared preferences`() {
        // Given
        sut.onKeyNameChanged("bobble")

        // When
        sut.onPromptSuccess(mockAuthResult)

        // Then
        verifyBlocking(mockBiometricSharedPreferencesController) { retrieveEncryptedMessage("bobble") }
        verifyBlocking(mockSecretController) { decryptData(eq(mockEncryptedMessage)) }
        verify(decryptedTextEventObserver).onChanged(decryptedTextEventCaptor.capture())
        assertEquals("wibble", decryptedTextEventCaptor.lastValue.getContent())
    }

    @Test
    fun `Shared preferences cannot find the key`() {
        // Given
        sut.onKeyNameChanged("wibble")
        runBlocking {
            whenever(mockBiometricSharedPreferencesController.retrieveEncryptedMessage(any())).thenReturn(null)
        }

        // When
        sut.onPromptSuccess(mockAuthResult)

        // Then
        verifyBlocking(mockBiometricSharedPreferencesController) { retrieveEncryptedMessage("wibble") }
        verifyZeroInteractions(mockSecretController)
        verifyZeroInteractions(decryptedTextEventObserver)
    }

    @Test
    fun `Null return from secret controller`() {
        // Given
        sut.onKeyNameChanged("wibble")
        runBlocking {
            whenever(mockSecretController.decryptData(any())).thenReturn(null)
        }

        // When
        sut.onPromptSuccess(mockAuthResult)

        // Then
        verifyBlocking(mockBiometricSharedPreferencesController) { retrieveEncryptedMessage("wibble") }
        verifyBlocking(mockSecretController) { decryptData(eq(mockEncryptedMessage)) }
        verifyZeroInteractions(decryptedTextEventObserver)
    }
}