package com.hbrinj.biometricredentialspoc.core.module

import android.security.keystore.KeyProperties
import com.hbrinj.biometricredentialspoc.core.module.CryptoModule.Constants.ANDROID_KEYSTORE
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import java.security.KeyStore

@Module
@InstallIn(SingletonComponent::class)
object CryptoModule {
    @Provides
    fun secretCipher() : Cipher {
        return Cipher.getInstance(
            "${KeyProperties.KEY_ALGORITHM_AES}/${KeyProperties.BLOCK_MODE_CBC}/${KeyProperties.ENCRYPTION_PADDING_PKCS7}"
        )
    }

    @Provides
    fun secretKeystore() : KeyStore {
        return KeyStore.getInstance(ANDROID_KEYSTORE)
    }

    @Provides
    fun keyGenerator() : KeyGenerator {
        return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
    }

    private object Constants {
        const val ANDROID_KEYSTORE = "AndroidKeyStore"
    }
}