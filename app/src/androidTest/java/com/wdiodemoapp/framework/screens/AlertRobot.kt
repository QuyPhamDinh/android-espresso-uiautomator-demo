package com.wdiodemoapp.framework.screens

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.containsString

/**
 * Robot for interacting with native Android AlertDialog windows.
 */
object AlertRobot {

    fun assertTitleOrMessageContains(expected: String) = apply {
        onView(withText(containsString(expected)))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
    }

    fun assertMessageContains(expected: String) = apply {
        onView(allOf(withId(android.R.id.message), withText(containsString(expected))))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
    }

    fun tapButton(buttonText: String) = apply {
        onView(withText(buttonText))
            .inRoot(isDialog())
            .perform(click())
    }

    fun assertNoDialogWithText(text: String) = apply {
        onView(withText(containsString(text))).check(doesNotExist())
    }
}
