package com.hbrinj.biometricredentialspoc.features.main.robot

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.hbrinj.biometricredentialspoc.R
import com.hbrinj.biometricredentialspoc.features.encryptSecret.robot.EncryptRobot
import com.hbrinj.biometricredentialspoc.features.main.robot.MainConstants.DECRYPT_BUTTON_TEXT
import com.hbrinj.biometricredentialspoc.features.main.robot.MainConstants.ENCRYPT_BUTTON_TEXT

class MainRobot {

    fun checkEncryptButton() = apply {
        onView(withId(R.id.main_button_encrypt))
            .check(ViewAssertions.matches(withText(ENCRYPT_BUTTON_TEXT)))
    }

    fun checkDecryptButton() = apply {
        onView(withId(R.id.main_button_decrypt))
            .check(ViewAssertions.matches(withText(DECRYPT_BUTTON_TEXT)))
    }

    fun encrypt(): EncryptRobot {
        onView(withId(R.id.main_button_encrypt)).perform(click())
        return EncryptRobot()
    }

    fun decrypt(): MainRobot {
        onView(withId(R.id.main_button_encrypt)).perform(click())
        return MainRobot()
    }
}