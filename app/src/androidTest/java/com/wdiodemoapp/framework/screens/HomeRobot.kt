package com.wdiodemoapp.framework.screens

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.wdiodemoapp.framework.core.CustomMatchers.clickableControlsHaveAccessibleNames
import com.wdiodemoapp.framework.core.EspressoActions.assertDisplayed
import com.wdiodemoapp.testshared.AccessibilityIds
import org.hamcrest.Matchers.containsString

/**
 * Robot for Home screen assertions and accessibility inspections.
 */
object HomeRobot {

    fun open() = apply {
        NavigationRobot.openHome()
        assertVisible()
    }

    fun assertVisible() = apply {
        assertDisplayed(AccessibilityIds.Screens.HOME)
    }

    fun assertReadableText(vararg textSnippets: String) = apply {
        assertVisible()
        textSnippets.forEach { snippet ->
            onView(withText(containsString(snippet))).check(matches(isDisplayed()))
        }
    }

    fun assertAllClickableControlsNamed() = apply {
        assertVisible()
        onView(isRoot()).check(clickableControlsHaveAccessibleNames())
    }
}
