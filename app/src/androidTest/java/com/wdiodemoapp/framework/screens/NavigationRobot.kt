package com.wdiodemoapp.framework.screens

import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isFocusable
import androidx.test.espresso.matcher.ViewMatchers.isSelected
import androidx.test.espresso.assertion.ViewAssertions.matches
import com.wdiodemoapp.framework.core.CustomMatchers.hasMinimumTouchTargetDp
import com.wdiodemoapp.framework.core.EspressoActions.onDesc
import com.wdiodemoapp.framework.core.EspressoActions.tapDesc
import com.wdiodemoapp.testshared.AccessibilityIds
import com.wdiodemoapp.testshared.SystemActions
import org.hamcrest.Matchers.allOf

/**
 * Robot for bottom tab navigation and wdio:// deep-link routing.
 */
object NavigationRobot {

    fun openHome() = openTab(AccessibilityIds.Tabs.HOME)
    fun openWebView() = openTab(AccessibilityIds.Tabs.WEBVIEW)
    fun openLogin() = openTab(AccessibilityIds.Tabs.LOGIN)
    fun openForms() = openTab(AccessibilityIds.Tabs.FORMS)
    fun openSwipe() = openTab(AccessibilityIds.Tabs.SWIPE)
    fun openDrag() = openTab(AccessibilityIds.Tabs.DRAG)

    fun openTab(tabContentDesc: String) = apply {
        tapDesc(tabContentDesc)
    }

    fun openDeepLink(system: SystemActions, route: String) = apply {
        system.openDeepLink(route)
    }

    fun assertTabBarReady() = apply {
        onDesc(AccessibilityIds.Tabs.HOME).check(matches(isDisplayed()))
    }

    fun assertTabSelected(tabContentDesc: String) = apply {
        onDesc(tabContentDesc).check(matches(isSelected()))
    }

    fun assertTabUsableTouchTarget(tabContentDesc: String) = apply {
        onDesc(tabContentDesc).check(
            matches(
                allOf(
                    isDisplayed(),
                    isFocusable(),
                    isClickable(),
                    hasMinimumTouchTargetDp(47.5f),
                ),
            ),
        )
    }
}
