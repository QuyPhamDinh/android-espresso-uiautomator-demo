package com.wdiodemoapp.framework.screens

import android.os.SystemClock
import android.view.View
import androidx.test.espresso.Espresso.onIdle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.viewpager2.widget.ViewPager2
import com.wdiodemoapp.framework.core.EspressoActions.assertDisplayed
import com.wdiodemoapp.framework.core.EspressoActions.onDesc
import com.wdiodemoapp.testshared.AccessibilityIds
import com.wdiodemoapp.testshared.TestConfig
import org.hamcrest.Matcher

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
        onDesc(AccessibilityIds.Swipe.CAROUSEL).perform(ViewActions.swipeLeft(), waitForCarouselIdle())
        onIdle()
    }

    fun swipeRight(): SwipeRobot = apply {
        onDesc(AccessibilityIds.Swipe.CAROUSEL).perform(ViewActions.swipeRight(), waitForCarouselIdle())
        onIdle()
    }

    private fun waitForCarouselIdle(): ViewAction = object : ViewAction {
        override fun getConstraints(): Matcher<View> = isAssignableFrom(ViewPager2::class.java)
        override fun getDescription(): String = "wait for ViewPager2 scroll state to become idle"

        override fun perform(uiController: UiController, view: View) {
            val pager = view as ViewPager2
            val deadline = SystemClock.elapsedRealtime() + TestConfig.uiTimeoutMs
            while (pager.scrollState != ViewPager2.SCROLL_STATE_IDLE) {
                check(SystemClock.elapsedRealtime() < deadline) {
                    "Timed out waiting for ViewPager2 scrollState to become IDLE (state=${pager.scrollState})"
                }
                uiController.loopMainThreadForAtLeast(25)
            }
        }
    }

    fun scrollToSurpriseLogo() = apply {
        onView(withContentDescription(AccessibilityIds.Swipe.LOGO))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
    }
}
