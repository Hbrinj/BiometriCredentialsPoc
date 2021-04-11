package com.hbrinj.biometricredentialspoc.core.biometric

import android.content.SharedPreferences
import com.google.gson.Gson
import com.hbrinj.biometricredentialspoc.CoroutineExtension
import com.hbrinj.biometricredentialspoc.core.crypto.EncryptedMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyZeroInteractions
import org.mockito.kotlin.whenever

class BiometricSharedPreferencesControllerTest {

    private val mockSharedPreferencesEditor: SharedPreferences.Editor = mock()
    private val mockEncryptedMessage: EncryptedMessage = mock()
    private val mockSharedPreferences: SharedPreferences = mock {
        on { edit() } doReturn mockSharedPreferencesEditor
        on { getString(any(), any()) } doReturn "bobble"
    }
    private val mockGSON: Gson = mock {
        on { toJson(any(EncryptedMessage::class.java)) } doReturn "wobble"
        on { fromJson(anyString(), eq(EncryptedMessage::class.java)) } doReturn mockEncryptedMessage
    }

    companion object {
        @ExperimentalCoroutinesApi
        private val testDispatcher = TestCoroutineDispatcher()

        @ExperimentalCoroutinesApi
        @RegisterExtension
        @JvmField
        val coroutineExtension = CoroutineExtension(testDispatcher)
    }

    private lateinit var sut: BiometricSharedPreferencesController

    @ExperimentalCoroutinesApi
    @BeforeEach
    fun setup() {

        sut = BiometricSharedPreferencesController(mockSharedPreferences, mockGSON, testDispatcher)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `Saving Encrypted message converts to json before saving`() = testDispatcher.runBlockingTest {
        // Given

        // When
        sut.saveEncryptedMessage("wibble", mockEncryptedMessage)

        // Then
        verify(mockGSON).toJson(eq(mockEncryptedMessage))
        verify(mockSharedPreferences).edit()
        verify(mockSharedPreferencesEditor).putString(eq("wibble"), eq("wobble"))
        verify(mockSharedPreferencesEditor).apply()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `Retrieving an encrypted message returns the object if found`() = testDispatcher.runBlockingTest {
        // Given

        // When
        val result = sut.retrieveEncryptedMessage("gobble")

        // Then
        verify(mockSharedPreferences).getString(eq("gobble"), eq(null))
        verify(mockGSON).fromJson(eq("bobble"),eq(EncryptedMessage::class.java))
        assertEquals(mockEncryptedMessage, result)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `Retrieving an encrypted message returns null if the object is not found`() = testDispatcher.runBlockingTest {
        // Given
        whenever(mockSharedPreferences.getString(eq("gobble"), eq(null))).thenReturn(null)

        // When
        val result = sut.retrieveEncryptedMessage("gobble")

        // Then
        verify(mockSharedPreferences).getString(eq("gobble"), eq(null))
        verifyZeroInteractions(mockGSON)
        assertNull(result)
    }
}