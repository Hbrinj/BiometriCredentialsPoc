package com.hbrinj.biometricredentialspoc.core.biometric

import android.content.SharedPreferences
import com.google.gson.Gson
import com.hbrinj.biometricredentialspoc.core.crypto.EncryptedMessage
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class BiometricSharedPreferencesController @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson,
    private val dispatcher: CoroutineDispatcher
) {
    suspend fun saveEncryptedMessage(name: String, encryptedMessage: EncryptedMessage) {
        withContext(dispatcher) {
            val jsonBlob = gson.toJson(encryptedMessage)
            sharedPreferences.edit().apply {
                putString(name, jsonBlob)
            }.apply()
        }
    }

    suspend fun retrieveEncryptedMessage(name: String): EncryptedMessage? {
        return withContext(dispatcher) {
            sharedPreferences.getString(name, null)?.let {
                gson.fromJson(it, EncryptedMessage::class.java)
            }
        }
    }
}