package com.hbrinj.biometricredentialspoc.core.crypto

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

internal class EncryptedMessageTest {
    @ParameterizedTest
    @MethodSource("messages")
    fun `Given two EncryptedMessages are different`(other: EncryptedMessage, equal: Boolean) {
        // Given
        val sut = EncryptedMessage("hello".toByteArray(),"world".toByteArray())

        // When
        val result = sut == other

        // Then
        assertEquals(result, equal)
        if(equal){
            assertEquals(sut.hashCode(), other.hashCode())
        } else {
            assertNotEquals(sut.hashCode(), other.hashCode())
        }
    }

    companion object{
        @JvmStatic
        private fun messages(): Stream<Arguments> {
            return Stream.of(
                arguments(EncryptedMessage("hello".toByteArray(),"world".toByteArray()), true),
                arguments(EncryptedMessage("hello1".toByteArray(),"world".toByteArray()), false),
                arguments(EncryptedMessage("hello".toByteArray(),"world1".toByteArray()), false)
            )
        }
    }
}