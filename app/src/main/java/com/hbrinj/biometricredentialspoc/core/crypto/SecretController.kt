package com.hbrinj.biometricredentialspoc.core.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.hbrinj.biometricredentialspoc.core.crypto.SecretController.Constants.ANDROID_KEYSTORE
import com.hbrinj.biometricredentialspoc.core.crypto.SecretController.Constants.KEY_NAME
import com.hbrinj.biometricredentialspoc.core.crypto.SecretController.Constants.KEY_SIZE
import com.hbrinj.biometricredentialspoc.core.crypto.SecretController.Constants.KEY_TIMEOUT
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.inject.Inject
import java.security.KeyStore

class SecretController @Inject constructor() {
    private fun getCipherForEncryption(): Cipher {
        val cipher = getCipher()
        val key = getOrGenerateSecretKey()
        cipher.init(Cipher.ENCRYPT_MODE, key)
        return cipher
    }

    private fun getCipherForDecryption(iv : ByteArray): Cipher {
        val cipher = getCipher()
        val key = getOrGenerateSecretKey()
        cipher.init(
            Cipher.DECRYPT_MODE,
            key,
            IvParameterSpec(iv)
        )
        return cipher
    }

    fun encryptData(data: ByteArray): EncryptedMessage {
        val encryptionCipher = getCipherForEncryption()
        val encryptedBlob = encryptionCipher.doFinal(data)
        return EncryptedMessage(encryptedBlob, encryptionCipher.iv)
    }

    fun decryptData(encryptedMessage: EncryptedMessage): ByteArray {
        val decryptionCipher = getCipherForDecryption(encryptedMessage.iv)
        return decryptionCipher.doFinal(encryptedMessage.encryptedMessage)
    }

    private fun getOrGenerateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)
        keyStore.getKey(KEY_NAME, null)?.let { return it as SecretKey }

        val keyGenParameterSpec = getKeySpec()
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        keyGenerator.init(keyGenParameterSpec)
        return keyGenerator.generateKey()
    }

    private fun getCipher(): Cipher {
        return Cipher.getInstance(
            "${KeyProperties.KEY_ALGORITHM_AES}/${KeyProperties.BLOCK_MODE_CBC}/${KeyProperties.ENCRYPTION_PADDING_PKCS7}"
        )
    }

    private fun getKeySpec() = KeyGenParameterSpec.Builder(
        KEY_NAME,
        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
    )
        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
        .setKeySize(KEY_SIZE)
        .setUserAuthenticationRequired(true)
        .setUserAuthenticationParameters(KEY_TIMEOUT, KeyProperties.AUTH_DEVICE_CREDENTIAL)
        .setInvalidatedByBiometricEnrollment(true)
        .build()

    private object Constants {
        const val KEY_NAME = "KEY_NAME"
        const val ANDROID_KEYSTORE = "AndroidKeyStore"
        const val KEY_SIZE = 256
        const val KEY_TIMEOUT = 2
    }
}