package com.wdiodemoapp.framework.screens

import android.widget.EditText
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.isNotChecked
import androidx.test.espresso.matcher.ViewMatchers.isNotEnabled
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.wdiodemoapp.framework.core.EspressoActions.assertDisplayed
import com.wdiodemoapp.framework.core.EspressoActions.onDesc
import com.wdiodemoapp.framework.core.EspressoActions.replaceTextAndCloseKeyboard
import com.wdiodemoapp.framework.core.EspressoActions.scrollIfNeededAndTap
import com.wdiodemoapp.framework.core.EspressoActions.tapDesc
import com.wdiodemoapp.testshared.AccessibilityIds
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.Matchers.`is`

/**
 * Robot for Forms screen controls (input, mirror text, switch, dropdown picker, buttons).
 */
object FormsRobot {

    fun open() = apply {
        NavigationRobot.openForms()
        assertVisible()
    }

    fun assertVisible() = apply {
        assertDisplayed(AccessibilityIds.Screens.FORMS)
    }

    fun enterText(text: String) = apply {
        replaceTextAndCloseKeyboard(AccessibilityIds.Forms.TEXT_INPUT, text)
    }

    fun assertInputText(expected: String) = apply {
        onDesc(AccessibilityIds.Forms.TEXT_INPUT).check(matches(withText(expected)))
    }

    fun assertInputEmpty() = apply {
        onDesc(AccessibilityIds.Forms.TEXT_INPUT).check(matches(withText("")))
    }

    fun assertMirrorContains(expected: String) = apply {
        onDesc(AccessibilityIds.Forms.INPUT_RESULT)
            .check(matches(withText(containsString(expected))))
    }

    fun setSwitch(on: Boolean) = apply {
        try {
            assertSwitchChecked(on)
        } catch (_: AssertionError) {
            tapDesc(AccessibilityIds.Forms.SWITCH)
            assertSwitchChecked(on)
        }
    }

    fun assertSwitchChecked(expected: Boolean) = apply {
        onDesc(AccessibilityIds.Forms.SWITCH)
            .check(matches(if (expected) isChecked() else isNotChecked()))
    }

    fun selectDropdownOption(optionText: String) = apply {
        tapDesc(AccessibilityIds.Forms.DROPDOWN)
        onData(allOf(`is`(instanceOf(String::class.java)), `is`(optionText)))
            .inRoot(isDialog())
            .perform(click())
    }

    fun assertDropdownContains(expected: String) = apply {
        onView(
            allOf(
                isAssignableFrom(EditText::class.java),
                isDescendantOfA(withContentDescription(AccessibilityIds.Forms.DROPDOWN)),
            ),
        ).check(matches(withText(containsString(expected))))
    }

    fun tapActiveButton() = apply {
        scrollIfNeededAndTap(withContentDescription(AccessibilityIds.Forms.ACTIVE_BUTTON))
    }

    fun assertInactiveButtonDisabled() = apply {
        scrollIfNeededAndTap(withContentDescription(AccessibilityIds.Forms.INACTIVE_BUTTON))
        onDesc(AccessibilityIds.Forms.INACTIVE_BUTTON).check(matches(isNotEnabled()))
    }

    fun assertStateCleared() = apply {
        assertInputEmpty()
        assertSwitchChecked(false)
    }
}
