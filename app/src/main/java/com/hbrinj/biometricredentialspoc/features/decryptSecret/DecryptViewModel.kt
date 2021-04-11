package com.hbrinj.biometricredentialspoc.features.decryptSecret

import androidx.biometric.BiometricPrompt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hbrinj.biometricredentialspoc.core.architecture.DataEvent
import com.hbrinj.biometricredentialspoc.core.biometric.BiometricSharedPreferencesController
import com.hbrinj.biometricredentialspoc.core.biometric.BiometricsController
import com.hbrinj.biometricredentialspoc.core.crypto.SecretController
import com.hbrinj.biometricredentialspoc.features.encryptSecret.BiometricViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import java.nio.charset.Charset

@HiltViewModel
class DecryptViewModel @Inject constructor(
    biometricsController: BiometricsController,
    private val secretController: SecretController,
    private val biometricSharedPreferencesController: BiometricSharedPreferencesController
): BiometricViewModel(biometricsController) {

    private val _decryptedTextEvent = MutableLiveData<DataEvent<String>>()
    val decryptedTextEvent: LiveData<DataEvent<String>> = _decryptedTextEvent

    private var keyName: CharSequence = ""

    override fun onPromptSuccess(result: BiometricPrompt.AuthenticationResult) {
        decryptSecret()
        super.onPromptSuccess(result)
    }

    fun onKeyNameChanged(text: CharSequence?) {
        text?.let {
            keyName = text
        }
    }

    private fun decryptSecret() {
        viewModelScope.launch {
            biometricSharedPreferencesController.retrieveEncryptedMessage(keyName.toString())?.let { encryptedMessage ->
                secretController.decryptData(encryptedMessage)?.let { decryptedMessage ->
                    val data = String(decryptedMessage, Charset.defaultCharset())
                    _decryptedTextEvent.postValue(DataEvent(data))
                } // Do something here if we were unable to decrypt
            }
        }
    }
}