package com.hbrinj.biometricredentialspoc.features.encryptSecret.robot

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withHint
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.hbrinj.biometricredentialspoc.R
import com.hbrinj.biometricredentialspoc.features.encryptSecret.robot.EncryptConstants.ENCRYPT_BUTTON
import com.hbrinj.biometricredentialspoc.features.encryptSecret.robot.EncryptConstants.ENCRYPT_KEY_HINT
import com.hbrinj.biometricredentialspoc.features.encryptSecret.robot.EncryptConstants.ENCRYPT_SECRET_HINT

class EncryptRobot {

    fun checkEncryptButton() = apply {
        onView(withId(R.id.encrypt_button))
            .check(matches(ViewMatchers.withText(ENCRYPT_BUTTON)))
    }

    fun checkKeyEditText() = apply {
        onView(withId(R.id.encrypt_key_name))
            .check(matches(withHint(ENCRYPT_KEY_HINT)))
    }

    fun checkSecretEditText() = apply {
        onView(withId(R.id.encrypt_secret))
            .check(matches(withHint(ENCRYPT_SECRET_HINT)))
    }

    fun checkAll() = apply {
        checkEncryptButton()
        checkKeyEditText()
        checkSecretEditText()
    }
}