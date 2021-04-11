package com.hbrinj.biometricredentialspoc.core.crypto

import com.hbrinj.biometricredentialspoc.CoroutineExtension
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.mockito.Mockito
import org.mockito.kotlin.KArgumentCaptor
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import java.security.InvalidKeyException
import java.security.KeyStore

internal class SecretControllerTest {

    private val mockData: ByteArray = "hello".toByteArray()
    private val mockEncryptedData: ByteArray = "Encrypted".toByteArray()
    private val mockIv: ByteArray = "Iv".toByteArray()
    private val mockSecretKey: SecretKey = mock()
    private val ivCaptor: KArgumentCaptor<IvParameterSpec> = argumentCaptor()
    private val encryptedMessage = EncryptedMessage(mockEncryptedData, mockIv)
    private val mockCipher: Cipher = mock{
        on { doFinal(any()) } doReturn mockEncryptedData
        on { iv } doReturn mockIv
    }
    private val mockKeyStore: KeyStore = mock {
        on { getKey(any(), eq(null)) } doReturn mockSecretKey
    }
    private val mockKeyGenerator: KeyGenerator = mock()

    companion object {
        @ExperimentalCoroutinesApi
        private val testDispatcher = TestCoroutineDispatcher()

        @ExperimentalCoroutinesApi
        @RegisterExtension
        @JvmField
        val coroutineExtension = CoroutineExtension(testDispatcher)
    }

    private lateinit var sut: SecretController

    @ExperimentalCoroutinesApi
    @BeforeEach
    fun setup() {
        sut = SecretController(mockCipher, mockKeyStore, mockKeyGenerator, testDispatcher)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `Encrypting Data initialises in EncryptMode with a secretKey and encrypts on do final`() = testDispatcher.runBlockingTest {
        // Given

        // When
        val result = sut.encryptData(mockData)

        // Then
        verify(mockCipher).init(eq(Cipher.ENCRYPT_MODE), eq(mockSecretKey))
        verify(mockCipher).doFinal(eq(mockData))
        assertEquals(result!!.encryptedMessage, mockEncryptedData)
        assertEquals(result.iv, mockIv)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `Decrypting Data initialises in DecryptMode with a secretKey and decrypts on do final`() = testDispatcher.runBlockingTest {
        // Given

        whenever(mockCipher.doFinal(eq(mockEncryptedData))).thenReturn(mockData)

        // When
        val result = sut.decryptData(encryptedMessage)

        // Then
        verify(mockCipher).init(eq(Cipher.DECRYPT_MODE), eq(mockSecretKey), ivCaptor.capture())
        assertArrayEquals(mockIv, ivCaptor.lastValue.iv)
        verify(mockCipher).doFinal(eq(mockEncryptedData))
        assertArrayEquals(mockData, result)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `Initialising the Cipher for encryption throws invalidKeyException return null`() = testDispatcher.runBlockingTest {
        // Given
        whenever(mockCipher.init(any(), eq(mockSecretKey))).thenThrow(InvalidKeyException("some invalid key thing"))

        // When
        val result = sut.encryptData(mockData)

        // Then
        assertNull(result)
        verify(mockCipher).init(eq(Cipher.ENCRYPT_MODE), eq(mockSecretKey))
        verifyNoMoreInteractions(mockCipher)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `Encrypting the data, do final throws an Exception return null`() = testDispatcher.runBlockingTest {
        /// Given
        whenever(mockCipher.doFinal(any())).thenThrow(IllegalBlockSizeException("block size"))

        // When
        val result = sut.encryptData(mockData)

        // Then
        assertNull(result)
        verify(mockCipher).init(eq(Cipher.ENCRYPT_MODE), eq(mockSecretKey))
        verify(mockCipher).doFinal(eq(mockData))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `Initialising the Cipher for decryption throws invalidKeyException return null`() = testDispatcher.runBlockingTest {
        // Given
        whenever(mockCipher.init(any(), eq(mockSecretKey), Mockito.any(IvParameterSpec::class.java)))
            .thenThrow(InvalidKeyException("some invalid key thing"))

        // When
        val result = sut.decryptData(encryptedMessage)

        // Then
        assertNull(result)
        verify(mockCipher).init(eq(Cipher.DECRYPT_MODE), eq(mockSecretKey), ivCaptor.capture())
        assertArrayEquals(mockIv, ivCaptor.lastValue.iv)
        verifyNoMoreInteractions(mockCipher)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `Decrypting the data, do final throws an Exception return null`() = testDispatcher.runBlockingTest {
        /// Given
        whenever(mockCipher.doFinal(any())).thenThrow(IllegalBlockSizeException("block size"))

        // When
        val result = sut.decryptData(encryptedMessage)

        // Then
        assertNull(result)
        verify(mockCipher).init(eq(Cipher.DECRYPT_MODE), eq(mockSecretKey), ivCaptor.capture())
        assertArrayEquals(mockIv, ivCaptor.lastValue.iv)
        verify(mockCipher).doFinal(eq(mockEncryptedData))
    }

}