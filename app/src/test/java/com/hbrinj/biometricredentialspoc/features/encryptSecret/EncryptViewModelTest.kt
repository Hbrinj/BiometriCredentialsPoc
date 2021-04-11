package com.hbrinj.biometricredentialspoc.features.encryptSecret

import androidx.biometric.BiometricPrompt
import com.hbrinj.biometricredentialspoc.InstantTaskExecutionExtension
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
import org.mockito.kotlin.verifyBlocking
import org.mockito.kotlin.verifyZeroInteractions
import org.mockito.kotlin.whenever
import java.nio.charset.Charset

@ExtendWith(InstantTaskExecutionExtension::class)
internal class EncryptViewModelTest {

    private val mockEncryptedMessage: EncryptedMessage = mock()
    private val mockBiometricsController: BiometricsController = mock {
        on {enrollmentState} doReturn EnrollmentState.Enrolled
    }
    private val mockSecretController: SecretController = mock {
        onBlocking { encryptData(any()) } doReturn mockEncryptedMessage
    }
    private val mockBiometricSharedPreferencesController: BiometricSharedPreferencesController = mock()
    private val mockAuthResult: BiometricPrompt.AuthenticationResult = mock()
    private val keyNameCaptor: KArgumentCaptor<String> = argumentCaptor()
    private val secretMessageCaptor: KArgumentCaptor<ByteArray> = argumentCaptor()

    private lateinit var sut: EncryptViewModel

    @BeforeEach
    fun setup() {
        sut = EncryptViewModel(
            mockBiometricsController,
            mockSecretController,
            mockBiometricSharedPreferencesController
        )
    }

    @Test
    fun `Correctly use the keyname and secret to encrypt and save to shared preferences`() {
        // Given
        sut.keyNameChanged("wibble")
        sut.secretMessageChanged("wobble")

        // When
        sut.onPromptSuccess(mockAuthResult)

        // Then
        verifyBlocking(mockSecretController) { encryptData(secretMessageCaptor.capture()) }
        assertEquals("wobble", String(secretMessageCaptor.lastValue, Charset.defaultCharset()))
        verifyBlocking(mockBiometricSharedPreferencesController) { saveEncryptedMessage(keyNameCaptor.capture(), eq(mockEncryptedMessage)) }
        assertEquals("wibble", keyNameCaptor.lastValue)
    }

    @Test
    fun `If data encryption fails don't call biometric Shared Preferences controller`() {
        // Given
        sut.keyNameChanged("wibble")
        sut.secretMessageChanged("wobble")
        runBlocking {
            whenever(mockSecretController.encryptData(any())).thenReturn(null)
        }

        // When
        sut.onPromptSuccess(mockAuthResult)

        // Then
        verifyBlocking(mockSecretController) { encryptData(secretMessageCaptor.capture()) }
        assertEquals("wobble", String(secretMessageCaptor.lastValue, Charset.defaultCharset()))
        verifyZeroInteractions(mockBiometricSharedPreferencesController)
    }
}