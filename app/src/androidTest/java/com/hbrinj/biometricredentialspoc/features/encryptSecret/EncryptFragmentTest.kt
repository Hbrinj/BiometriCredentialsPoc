package com.hbrinj.biometricredentialspoc.features.encryptSecret

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.hbrinj.biometricredentialspoc.features.main.MainActivity
import com.hbrinj.biometricredentialspoc.features.main.robot.MainRobot
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class EncryptFragmentTest {

    @get:Rule
    var activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun checkEncryptFragmentScreen() {
        MainRobot()
            .encrypt()
            .checkAll()
    }
}