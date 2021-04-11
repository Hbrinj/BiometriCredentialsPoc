package com.hbrinj.biometricredentialspoc.core.architecture

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class DataEventTest {

    @Test
    fun `When retrieving content from an Event, handled becomes true after retrieval`() {
        // Given
        val sut = DataEvent("hello")

        // When
        val result = sut.getContent()

        // Then
        assertTrue(sut.handled)
        assertEquals("hello", result)
    }

    @Test
    fun `When retrieving content from an Event, the second time round its null`() {
        // Given
        val sut = DataEvent("hello")
        sut.getContent()

        // When
        val result = sut.getContent()

        // Then
        assertTrue(sut.handled)
        assertNull(result)
    }

    @Test
    fun `Handled is false when the content hasn't been retrieved`() {
        // Given

        // When
        val sut = DataEvent("hello")

        // Then
        assertFalse(sut.handled)
    }
}