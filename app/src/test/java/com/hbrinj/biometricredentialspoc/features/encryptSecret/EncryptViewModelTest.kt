package com.hbrinj.biometricredentialspoc.features.encryptSecret

import androidx.biometric.BiometricPrompt
import androidx.lifecycle.Observer
import com.hbrinj.biometricredentialspoc.InstantTaskExecutionExtension
import com.hbrinj.biometricredentialspoc.core.architecture.DataEvent
import com.hbrinj.biometricredentialspoc.core.architecture.Event
import com.hbrinj.biometricredentialspoc.core.biometric.BiometricPromptCallback
import com.hbrinj.biometricredentialspoc.core.biometric.BiometricsController
import com.hbrinj.biometricredentialspoc.core.biometric.EnrollmentState
import com.hbrinj.biometricredentialspoc.core.crypto.SecretController
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.kotlin.KArgumentCaptor
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExtendWith(InstantTaskExecutionExtension::class)
internal class EncryptViewModelTest {
    private val mockBiometricsController: BiometricsController = mock {
        on {enrollmentState} doReturn EnrollmentState.Enrolled
    }
    private val mockSecretController: SecretController = mock()

    private val eligibilityEventObserver: Observer<DataEvent<EligibilityEvent>> = mock()
    private val eligibilityEventCaptor: KArgumentCaptor<DataEvent<EligibilityEvent>> = argumentCaptor()

    private val promptEventObserver: Observer<DataEvent<PromptEvent>> = mock()
    private val promptEventCaptor: KArgumentCaptor<DataEvent<PromptEvent>> = argumentCaptor()


    private lateinit var sut: EncryptViewModel

    @BeforeEach
    fun setup() {
        sut = EncryptViewModel(mockBiometricsController, mockSecretController)
        sut.eligibilityEvent.observeForever(eligibilityEventObserver)
        sut.promptEvent.observeForever(promptEventObserver)
    }

    @Test
    fun `Calling checkEligibilityAndPrompt starts the device or biometric auth if they are enrolled in what we expect`() {
        // Given

        // When
        sut.checkEligibilityAndPrompt()

        // Then
        verify(eligibilityEventObserver).onChanged(eligibilityEventCaptor.capture())
        assertTrue(eligibilityEventCaptor.lastValue.getContent() is EligibilityEvent.Prompt)
    }

    @Test
    fun `Calling checkEligibilityAndPrompt if the customer is not enrolled in a valid form of auth, push them to enroll`() {
        // Given
        whenever(mockBiometricsController.enrollmentState).thenReturn(EnrollmentState.CanEnroll)

        // When
        sut.checkEligibilityAndPrompt()

        // Then
        verify(eligibilityEventObserver).onChanged(eligibilityEventCaptor.capture())
        assertTrue(eligibilityEventCaptor.lastValue.getContent() is EligibilityEvent.CanEnroll)
    }

    @Test
    fun `Calling checkEligibilityAndPrompt if the customer does not have a valid form of auth, send the NotAvailable event`() {
        // Given
        whenever(mockBiometricsController.enrollmentState).thenReturn(EnrollmentState.NotAvailable)

        // When
        sut.checkEligibilityAndPrompt()

        // Then
        verify(eligibilityEventObserver).onChanged(eligibilityEventCaptor.capture())
        assertEquals(EligibilityEvent.NotAvailable, eligibilityEventCaptor.lastValue.getContent())
    }

    @Test
    fun `After the customer is prompted, if it is successful encrypt and save the data and post a successful event`() {
        // Given
        val callback = getPromptCallback()
        val mockAuthResult: BiometricPrompt.AuthenticationResult = mock()

        // When
        callback.onAuthenticationSucceeded(mockAuthResult)

        // Then
        verify(promptEventObserver).onChanged(promptEventCaptor.capture())
        assertEquals(PromptEvent.Successful, promptEventCaptor.firstValue.getContent())
    }

    @Test
    fun `After the customer is prompted, if it is a failure post a failure event`() {
        // Given
        val callback = getPromptCallback()

        // When
        callback.onAuthenticationFailed()

        // Then
        verify(promptEventObserver).onChanged(promptEventCaptor.capture())
        assertEquals(PromptEvent.Failure, promptEventCaptor.firstValue.getContent())
    }

    @Test
    fun `After the customer is prompted, if it is an error post an error event`() {
        // Given
        val callback = getPromptCallback()

        // When
        callback.onAuthenticationError(1, "test")

        // Then
        verify(promptEventObserver).onChanged(promptEventCaptor.capture())
        assertEquals(PromptEvent.Error, promptEventCaptor.firstValue.getContent())
    }

    private fun getPromptCallback(): BiometricPromptCallback {
        doNothing().whenever(eligibilityEventObserver).onChanged(eligibilityEventCaptor.capture())
        sut.checkEligibilityAndPrompt()
        return (eligibilityEventCaptor.firstValue.getContent() as EligibilityEvent.Prompt).callback
    }
}