package com.hbrinj.biometricredentialspoc.features.main

import androidx.lifecycle.Observer
import com.hbrinj.biometricredentialspoc.InstantTaskExecutionExtension
import com.hbrinj.biometricredentialspoc.core.architecture.Event
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.*


@ExtendWith(InstantTaskExecutionExtension::class)
internal class MainViewModelTest {

    lateinit var sut: MainViewModel

    private val encryptEventObserver: Observer<Event> = mock()
    private val decryptEventObserver: Observer<Event> = mock()

    @BeforeEach
    fun setup() {
        sut = MainViewModel()

        sut.encryptDataEvent.observeForever(encryptEventObserver)
        sut.decryptDataEvent.observeForever(decryptEventObserver)
    }

    @Test
    fun `when pressing the encrypt button emit a oneshot event to transition`() {
        // Given

        // When
        sut.encrypt()

        // Then
        verify(encryptEventObserver).onChanged(any())
    }

    @Test
    fun `when pressing the decrypt button emit a oneshot event to transition`() {
        // Given

        // When
        sut.decrypt()

        // Then
        verify(decryptEventObserver).onChanged(any())
    }

    @AfterEach
    fun check() {
        validateMockitoUsage()
        sut.encryptDataEvent.removeObserver(encryptEventObserver)
        sut.decryptDataEvent.removeObserver(decryptEventObserver)
    }
}