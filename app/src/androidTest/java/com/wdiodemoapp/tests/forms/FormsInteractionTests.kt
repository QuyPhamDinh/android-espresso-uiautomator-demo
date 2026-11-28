package com.wdiodemoapp.tests.forms

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.wdiodemoapp.framework.rules.AppHybridTestRule
import com.wdiodemoapp.framework.screens.AlertRobot
import com.wdiodemoapp.framework.screens.FormsRobot
import com.wdiodemoapp.testshared.AccessibilityIds
import com.wdiodemoapp.testshared.RegressionTest
import com.wdiodemoapp.testshared.SmokeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FormsInteractionTests {

    @get:Rule
    val app = AppHybridTestRule()

    @SmokeTest
    @Test
    fun textInputMirrorsTypedText() {
        val sample = "Hello, this is a demo app"
        FormsRobot.open()
            .enterText(sample)
            .assertMirrorContains(sample)
    }

    @SmokeTest
    @Test
    fun switchTogglesOnAndOff() {
        FormsRobot.open()
            .assertSwitchChecked(false)
            .setSwitch(true)
            .assertSwitchChecked(true)
            .setSwitch(false)
            .assertSwitchChecked(false)
    }

    @RegressionTest
    @Test
    fun dropdownSelectsAllThreeOptions() {
        FormsRobot.open()
        AccessibilityIds.Forms.DROPDOWN_OPTIONS.forEach { option ->
            FormsRobot.selectDropdownOption(option)
                .assertDropdownContains(option)
        }
    }

    @RegressionTest
    @Test
    fun activeButtonOpensAlertAndClosesWithAllThreeButtons() {
        FormsRobot.open()
        listOf("Ask me later", "Cancel", "OK").forEach { buttonLabel ->
            FormsRobot.tapActiveButton()
            AlertRobot.assertMessageContains("This button is active")
                .tapButton(buttonLabel)
                .assertNoDialogWithText("This button is")
        }
    }

    @RegressionTest
    @Test
    fun inactiveButtonRemainsDisabledAndShowsNoAlert() {
        FormsRobot.open()
            .assertInactiveButtonDisabled()
            .tapInactiveButton()
        AlertRobot.assertNoDialogWithText("This button is")
    }
}
