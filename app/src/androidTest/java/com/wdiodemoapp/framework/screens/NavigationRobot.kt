package com.wdiodemoapp.framework.screens

import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isFocusable
import androidx.test.espresso.matcher.ViewMatchers.isSelected
import com.wdiodemoapp.framework.core.CustomMatchers.hasMinimumTouchTargetDp
import com.wdiodemoapp.framework.core.EspressoActions.onDesc
import com.wdiodemoapp.framework.core.EspressoActions.tapDesc
import com.wdiodemoapp.testshared.AccessibilityIds
import com.wdiodemoapp.testshared.NavigationScreen
import com.wdiodemoapp.testshared.SystemActions
import org.hamcrest.Matchers.allOf

/**
 * Instantiable Espresso implementation of [NavigationScreen] for bottom tab and deep-link routing.
 */
open class EspressoNavigationRobot : NavigationScreen {

    override fun openHome(): EspressoNavigationRobot = openTab(AccessibilityIds.Tabs.HOME)
    override fun openWebView(): EspressoNavigationRobot = openTab(AccessibilityIds.Tabs.WEBVIEW)
    override fun openLogin(): EspressoNavigationRobot = openTab(AccessibilityIds.Tabs.LOGIN)
    override fun openForms(): EspressoNavigationRobot = openTab(AccessibilityIds.Tabs.FORMS)
    override fun openSwipe(): EspressoNavigationRobot = openTab(AccessibilityIds.Tabs.SWIPE)
    override fun openDrag(): EspressoNavigationRobot = openTab(AccessibilityIds.Tabs.DRAG)

    override fun openTab(tabContentDesc: String): EspressoNavigationRobot = apply {
        tapDesc(tabContentDesc)
    }

    fun openDeepLink(system: SystemActions, route: String): EspressoNavigationRobot = apply {
        system.openDeepLink(route)
    }

    override fun assertTabBarReady(): EspressoNavigationRobot = apply {
        onDesc(AccessibilityIds.Tabs.HOME).check(matches(isDisplayed()))
    }

    override fun assertTabSelected(tabContentDesc: String): EspressoNavigationRobot = apply {
        onDesc(tabContentDesc).check(matches(isSelected()))
    }

    fun assertTabUsableTouchTarget(tabContentDesc: String): EspressoNavigationRobot = apply {
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

/**
 * Default singleton entry point delegating to [EspressoNavigationRobot].
 */
object NavigationRobot : EspressoNavigationRobot()
