package com.wdiodemoapp.framework.core

import android.view.View
import android.widget.ScrollView
import androidx.core.widget.NestedScrollView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import org.hamcrest.Matcher
import org.hamcrest.Matchers.anyOf

/**
 * Shared Espresso interaction helpers handling scrollable containers and soft keyboard dismissal.
 */
object EspressoActions {

    fun onDesc(contentDescription: String): ViewInteraction =
        onView(withContentDescription(contentDescription))

    fun tapDesc(contentDescription: String) {
        scrollIfNeededAndTap(withContentDescription(contentDescription))
    }

    fun scrollIfNeededAndTap(matcher: Matcher<View>) {
        val view = onView(matcher)
        ensureVisibleInScrollable(view)
        view.perform(click())
    }

    fun ensureVisible(matcher: Matcher<View>): ViewInteraction {
        val view = onView(matcher)
        ensureVisibleInScrollable(view)
        return view
    }

    fun replaceTextAndCloseKeyboard(contentDescription: String, text: String) {
        val target = onDesc(contentDescription)
        ensureVisibleInScrollable(target)
        if (text.isEmpty()) {
            target.perform(clearText(), closeSoftKeyboard())
        } else {
            target.perform(replaceText(text), closeSoftKeyboard())
        }
    }

    private fun ensureVisibleInScrollable(view: ViewInteraction) {
        val needsScroll = try {
            view.check(matches(isDisplayingAtLeast(90)))
            false
        } catch (_: AssertionError) {
            true
        }
        if (needsScroll) {
            view.perform(scrollToIfInScrollable())
        }
    }

    private fun scrollToIfInScrollable(): ViewAction = object : ViewAction {
        override fun getConstraints(): Matcher<View> =
            isDescendantOfA(
                anyOf(
                    isAssignableFrom(ScrollView::class.java),
                    isAssignableFrom(NestedScrollView::class.java),
                ),
            )

        override fun getDescription(): String = "scroll to view inside ScrollView"

        override fun perform(uiController: UiController, view: View) {
            try {
                scrollTo().perform(uiController, view)
            } catch (error: PerformException) {
                throw error
            }
        }
    }

    fun assertDisplayed(contentDescription: String) {
        onDesc(contentDescription).check(matches(isDisplayed()))
    }
}
