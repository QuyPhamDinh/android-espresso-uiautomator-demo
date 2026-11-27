package com.wdiodemoapp.tests.accessibility

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.wdiodemoapp.framework.rules.AppHybridTestRule
import com.wdiodemoapp.framework.screens.FormsRobot
import com.wdiodemoapp.testshared.AccessibilityTest
import com.wdiodemoapp.testshared.FontScaleRule
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

/**
 * Hybrid test: UIAutomator FontScaleRule mutates Settings.System.FONT_SCALE to 2.0x,
 * while Espresso exercises and verifies Forms UI behavior at large font size.
 */
@AccessibilityTest
@RunWith(AndroidJUnit4::class)
class FormsFontScaleTests {

    @get:Rule
    val rules: RuleChain = RuleChain
        .outerRule(FontScaleRule(2f))
        .around(AppHybridTestRule())

    @Test
    fun formsInputAndSwitchRemainUsableAtDoubleFontScale() {
        FormsRobot.open()
            .setSwitch(true)
            .assertSwitchChecked(true)
            .enterText("Large text draft")
            .assertInputText("Large text draft")
    }
}
