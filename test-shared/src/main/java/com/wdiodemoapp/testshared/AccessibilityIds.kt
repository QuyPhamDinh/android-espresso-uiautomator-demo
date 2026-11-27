package com.wdiodemoapp.testshared

/**
 * Shared accessibility / locator contract shared across Espresso Robots and UIAutomator tests.
 */
object AccessibilityIds {

    object Tabs {
        const val HOME = "Home"
        const val WEBVIEW = "Webview"
        const val LOGIN = "Login"
        const val FORMS = "Forms"
        const val SWIPE = "Swipe"
        const val DRAG = "Drag"
        val ALL = listOf(HOME, WEBVIEW, LOGIN, FORMS, SWIPE, DRAG)
    }

    object Screens {
        const val HOME = "Home-screen"
        const val LOGIN = "Login-screen"
        const val FORMS = "Forms-screen"
        const val SWIPE = "Swipe-screen"
        const val DRAG = "Drag-drop-screen"
    }

    object Login {
        const val HEADER_TEXT = "Login / Sign up Form"
        const val LOGIN_TAB = "button-login-container"
        const val SIGNUP_TAB = "button-sign-up-container"
        const val EMAIL = "input-email"
        const val PASSWORD = "input-password"
        const val REPEAT_PASSWORD = "input-repeat-password"
        const val LOGIN_BUTTON = "button-LOGIN"
        const val SIGNUP_BUTTON = "button-SIGN UP"
        const val BIOMETRIC_BUTTON = "button-biometric"
    }

    object Forms {
        const val TEXT_INPUT = "text-input"
        const val INPUT_RESULT = "input-text-result"
        const val SWITCH = "switch"
        const val SWITCH_TEXT = "switch-text"
        const val DROPDOWN = "Dropdown"
        const val DROPDOWN_CHEVRON = "dropdown-chevron"
        const val ACTIVE_BUTTON = "button-Active"
        const val INACTIVE_BUTTON = "button-Inactive"

        val DROPDOWN_OPTIONS = listOf(
            "This app is awesome",
            "webdriver.io is awesome",
            "Appium is awesome",
        )
    }

    object Swipe {
        const val CAROUSEL = "Carousel"
        const val LOGO = "WebdriverIO logo"
        val CARDS = (0..5).map { "__CAROUSEL_ITEM_${it}__" }
    }

    object Drag {
        val PUZZLE_PAIRS = listOf(
            "drag-l1" to "drop-l1",
            "drag-c1" to "drop-c1",
            "drag-r1" to "drop-r1",
            "drag-l2" to "drop-l2",
            "drag-c2" to "drop-c2",
            "drag-r2" to "drop-r2",
            "drag-l3" to "drop-l3",
            "drag-c3" to "drop-c3",
            "drag-r3" to "drop-r3",
        )
        const val RENEW = "renew"
        const val RETRY = "button-Retry"
    }
}
