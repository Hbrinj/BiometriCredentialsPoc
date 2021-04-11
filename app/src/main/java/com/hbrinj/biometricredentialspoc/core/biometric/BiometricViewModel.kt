package com.hbrinj.biometricredentialspoc.features.encryptSecret

import androidx.biometric.BiometricPrompt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hbrinj.biometricredentialspoc.core.architecture.DataEvent
import com.hbrinj.biometricredentialspoc.core.biometric.BiometricPromptCallback
import com.hbrinj.biometricredentialspoc.core.biometric.BiometricsController
import com.hbrinj.biometricredentialspoc.core.biometric.EnrollmentState
import javax.inject.Inject

open class BiometricViewModel @Inject constructor(
    private val biometricsController: BiometricsController,
) : ViewModel() {
    private val _eligibilityEvent = MutableLiveData<DataEvent<EligibilityEvent>>()
    val eligibilityEvent: LiveData<DataEvent<EligibilityEvent>> = _eligibilityEvent

    private val _promptEvent = MutableLiveData<DataEvent<PromptEvent>>()
    val promptEvent: LiveData<DataEvent<PromptEvent>> = _promptEvent

    val allowedAuth get() = biometricsController.allowedAuth

    fun checkEligibilityAndPrompt() {
        when(biometricsController.enrollmentState) {
            EnrollmentState.CanEnroll -> _eligibilityEvent.postValue(DataEvent(EligibilityEvent.CanEnroll))
            EnrollmentState.Enrolled -> {
                val callback = BiometricPromptCallback(this::onPromptSuccess, this::onPromptFailure, this:: onPromptError)
                _eligibilityEvent.postValue(DataEvent(EligibilityEvent.Prompt(callback)))
            }
            EnrollmentState.NotAvailable -> _eligibilityEvent.postValue(DataEvent(EligibilityEvent.NotAvailable))
        }
    }

    open fun onPromptSuccess(result: BiometricPrompt.AuthenticationResult) {
        _promptEvent.postValue(DataEvent(PromptEvent.Successful))
    }

    open fun onPromptFailure() {
        _promptEvent.postValue(DataEvent(PromptEvent.Failure))
    }

    open fun onPromptError(errorCode: Int, errorMessage: CharSequence) {
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