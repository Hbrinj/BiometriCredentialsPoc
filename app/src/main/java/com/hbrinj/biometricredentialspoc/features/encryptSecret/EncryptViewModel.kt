package com.hbrinj.biometricredentialspoc.features.encryptSecret

import androidx.biometric.BiometricPrompt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hbrinj.biometricredentialspoc.core.architecture.DataEvent
import com.hbrinj.biometricredentialspoc.core.architecture.Event
import com.hbrinj.biometricredentialspoc.core.biometric.BiometricPromptCallback
import com.hbrinj.biometricredentialspoc.core.biometric.BiometricsController
import com.hbrinj.biometricredentialspoc.core.biometric.EnrollmentState
import com.hbrinj.biometricredentialspoc.core.crypto.SecretController
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EncryptViewModel @Inject constructor(
    private val biometricsController: BiometricsController,
    private val secretController: SecretController
): ViewModel() {
    private val _eligibilityEvent = MutableLiveData<DataEvent<EligibilityEvent>>()
    val eligibilityEvent: LiveData<DataEvent<EligibilityEvent>> = _eligibilityEvent

    private val _promptEvent = MutableLiveData<DataEvent<PromptEvent>>()
    val promptEvent: LiveData<DataEvent<PromptEvent>> = _promptEvent

    fun checkEligibilityAndPrompt() {
        when(biometricsController.enrollmentState) {
            EnrollmentState.CanEnroll -> _eligibilityEvent.postValue(DataEvent(EligibilityEvent.CanEnroll))
            EnrollmentState.Enrolled -> {
                //TODO: this needs a test to verify its correct
                val callback = BiometricPromptCallback(this::onPromptSuccess, this::onPromptFailure, this:: onPromptError)
                _eligibilityEvent.postValue(DataEvent(EligibilityEvent.Prompt(callback)))
            }
            EnrollmentState.NotAvailable -> _eligibilityEvent.postValue(DataEvent(EligibilityEvent.NotAvailable))
        }
    }

    private fun onPromptSuccess(result: BiometricPrompt.AuthenticationResult) {
        val secret = secretController.encryptData("hello".toByteArray())
        //save to shared preferences
        _promptEvent.postValue(DataEvent(PromptEvent.Successful))
    }

    private fun onPromptFailure() {
        _promptEvent.postValue(DataEvent(PromptEvent.Failure))
    }

    private fun onPromptError(errorCode: Int, errorMessage: CharSequence) {
        _promptEvent.postValue(DataEvent(PromptEvent.Error))
    }
}

sealed class PromptEvent {
    object Successful: PromptEvent()
    object Failure: PromptEvent()
    object Error: PromptEvent()
}

sealed class EligibilityEvent {
    object CanEnroll : EligibilityEvent()
    object NotAvailable : EligibilityEvent()
    data class Prompt(val callback: BiometricPromptCallback): EligibilityEvent()
}