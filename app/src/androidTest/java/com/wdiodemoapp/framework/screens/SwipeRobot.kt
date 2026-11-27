package com.wdiodemoapp.framework.screens

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import com.wdiodemoapp.framework.core.EspressoActions.assertDisplayed
import com.wdiodemoapp.framework.core.EspressoActions.onDesc
import com.wdiodemoapp.testshared.AccessibilityIds

/**
 * Robot for Swipe horizontal carousel and vertical bottom reveal.
 */
object SwipeRobot {

    fun open() = apply {
        NavigationRobot.openSwipe()
        assertVisible()
    }

    fun assertVisible() = apply {
        assertDisplayed(AccessibilityIds.Screens.SWIPE)
    }

    fun assertCardDisplayed(index: Int) = apply {
        val cardDesc = AccessibilityIds.Swipe.CARDS[index]
        onDesc(cardDesc).check(matches(isDisplayed()))
    }

    fun swipeLeft(): SwipeRobot = apply {
        onDesc(AccessibilityIds.Swipe.CAROUSEL).perform(ViewActions.swipeLeft())
    }

    fun swipeRight(): SwipeRobot = apply {
        onDesc(AccessibilityIds.Swipe.CAROUSEL).perform(ViewActions.swipeRight())
    }

    fun scrollToSurpriseLogo() = apply {
        onView(withContentDescription(AccessibilityIds.Swipe.LOGO))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
    }
}
