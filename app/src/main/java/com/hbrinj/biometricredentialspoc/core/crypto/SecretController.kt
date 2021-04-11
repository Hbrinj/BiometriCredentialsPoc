package com.hbrinj.biometricredentialspoc.core.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.hbrinj.biometricredentialspoc.core.crypto.SecretController.Constants.KEY_NAME
import com.hbrinj.biometricredentialspoc.core.crypto.SecretController.Constants.KEY_SIZE
import com.hbrinj.biometricredentialspoc.core.crypto.SecretController.Constants.KEY_TIMEOUT
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.security.KeyStore

class SecretController @Inject constructor(
    private val cipher: Cipher,
    private val keyStore: KeyStore,
    private val keyGenerator: KeyGenerator,
    private val dispatcher: CoroutineDispatcher
) {

    private val keySpec get() = KeyGenParameterSpec.Builder(
        KEY_NAME,
        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
    ).setBlockModes(KeyProperties.BLOCK_MODE_CBC)
        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
        .setKeySize(KEY_SIZE)
        .setUserAuthenticationRequired(true)
        .setUserAuthenticationParameters(KEY_TIMEOUT, KeyProperties.AUTH_DEVICE_CREDENTIAL or KeyProperties.AUTH_BIOMETRIC_STRONG)
        .setInvalidatedByBiometricEnrollment(true)
        .build()

    private fun getCipherForEncryption() = cipher.apply {
        init(
            Cipher.ENCRYPT_MODE,
            getOrGenerateSecretKey()
        )
    }

    private fun getCipherForDecryption(iv : ByteArray) = cipher.apply {
        init(
            Cipher.DECRYPT_MODE,
            getOrGenerateSecretKey(),
            IvParameterSpec(iv)
        )
    }

    suspend fun encryptData(data: ByteArray): EncryptedMessage? {
        return withContext(dispatcher) {
            try {
                val encryptionCipher = getCipherForEncryption()
                val encryptedBlob = encryptionCipher.doFinal(data)
                EncryptedMessage(encryptedBlob, encryptionCipher.iv)
            } catch (exception: Exception) {
                // Send something to analytics
                null
            }
        }
    }

    suspend fun decryptData(encryptedMessage: EncryptedMessage): ByteArray? {
        return withContext(dispatcher) {
            try {
                val decryptionCipher = getCipherForDecryption(encryptedMessage.iv)
                decryptionCipher.doFinal(encryptedMessage.encryptedMessage)
            } catch (exception: Exception) {
                // Send something to analytics
                null
            }
        }
    }

    private fun getOrGenerateSecretKey(): SecretKey {
        keyStore.load(null)
        keyStore.getKey(KEY_NAME, null)?.let { return it as SecretKey }

        return keyGenerator.apply {
            init(keySpec)
        }.generateKey()
    }

    private object Constants {
        const val KEY_NAME = "SECRET_KEY_NAME"
        const val KEY_SIZE = 256
        const val KEY_TIMEOUT = 10
    }
}