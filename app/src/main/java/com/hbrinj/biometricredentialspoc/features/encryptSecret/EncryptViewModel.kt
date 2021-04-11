package com.hbrinj.biometricredentialspoc.features.encryptSecret

import androidx.biometric.BiometricPrompt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hbrinj.biometricredentialspoc.core.architecture.DataEvent
import com.hbrinj.biometricredentialspoc.core.architecture.Event
import com.hbrinj.biometricredentialspoc.core.biometric.BiometricPromptCallback
import com.hbrinj.biometricredentialspoc.core.biometric.BiometricSharedPreferencesController
import com.hbrinj.biometricredentialspoc.core.biometric.BiometricsController
import com.hbrinj.biometricredentialspoc.core.biometric.EnrollmentState
import com.hbrinj.biometricredentialspoc.core.crypto.SecretController
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

@HiltViewModel
class EncryptViewModel @Inject constructor(
    biometricsController: BiometricsController,
    private val secretController: SecretController,
    private val biometricSharedPreferencesController: BiometricSharedPreferencesController
): BiometricViewModel(biometricsController) {

    private var keyName: CharSequence = ""
    private var secretMessage: CharSequence = ""

    fun keyNameChanged(text: CharSequence?) {
        text?.let {
            keyName = it
        }
    }

    fun secretMessageChanged(text: CharSequence?) {
        text?.let {
            secretMessage = it
        }
    }

    override fun onPromptSuccess(result: BiometricPrompt.AuthenticationResult) {
        encryptSecret()
        super.onPromptSuccess(result)
    }

    private fun encryptSecret() {
        viewModelScope.launch {
            secretController.encryptData(secretMessage.toString().toByteArray())
                ?.let { encryptedMessage ->
                    biometricSharedPreferencesController.saveEncryptedMessage(
                        keyName.toString(),
                        encryptedMessage
                    )
                } // Do something here if this failed and returned a null
        }
    }
}
