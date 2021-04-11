package com.hbrinj.biometricredentialspoc

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext

class CoroutineExtension @ExperimentalCoroutinesApi constructor(
    private val testCoroutineDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()
): AfterAllCallback, BeforeAllCallback {
    @ExperimentalCoroutinesApi
    override fun afterAll(context: ExtensionContext?) {
        Dispatchers.resetMain()
    }

    @ExperimentalCoroutinesApi
    override fun beforeAll(context: ExtensionContext?) {
        Dispatchers.setMain(testCoroutineDispatcher)
    }
}