package com.wdiodemoapp.framework.screens

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.wdiodemoapp.framework.core.EspressoActions.assertDisplayed
import com.wdiodemoapp.framework.core.EspressoActions.onDesc
import com.wdiodemoapp.framework.core.EspressoActions.replaceTextAndCloseKeyboard
import com.wdiodemoapp.framework.core.EspressoActions.tapDesc
import com.wdiodemoapp.testshared.AccessibilityIds
import org.hamcrest.Matchers.not

/**
 * Robot for Login / Sign Up form interactions.
 */
object LoginRobot {

    fun open() = apply {
        NavigationRobot.openLogin()
        assertVisible()
    }

    fun assertVisible() = apply {
        assertDisplayed(AccessibilityIds.Screens.LOGIN)
    }

    fun switchToLogin() = apply {
        tapDesc(AccessibilityIds.Login.LOGIN_TAB)
        assertRepeatPasswordVisible(false)
    }

    fun switchToSignUp() = apply {
        tapDesc(AccessibilityIds.Login.SIGNUP_TAB)
        assertRepeatPasswordVisible(true)
    }

    fun assertRepeatPasswordVisible(visible: Boolean) = apply {
        onDesc(AccessibilityIds.Login.REPEAT_PASSWORD)
            .check(matches(if (visible) isDisplayed() else not(isDisplayed())))
    }

    fun dismissKeyboardViaHeader() = apply {
        onView(withText(AccessibilityIds.Login.HEADER_TEXT)).perform(click())
    }

    fun login(email: String, password: String) = apply {
        switchToLogin()
        replaceTextAndCloseKeyboard(AccessibilityIds.Login.EMAIL, email)
        replaceTextAndCloseKeyboard(AccessibilityIds.Login.PASSWORD, password)
        tapDesc(AccessibilityIds.Login.LOGIN_BUTTON)
    }

    fun signUp(email: String, password: String, repeatPassword: String = password) = apply {
        switchToSignUp()
        replaceTextAndCloseKeyboard(AccessibilityIds.Login.EMAIL, email)
        replaceTextAndCloseKeyboard(AccessibilityIds.Login.PASSWORD, password)
        replaceTextAndCloseKeyboard(AccessibilityIds.Login.REPEAT_PASSWORD, repeatPassword)
        tapDesc(AccessibilityIds.Login.SIGNUP_BUTTON)
    }
}
