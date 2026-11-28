package com.wdiodemoapp.testshared

/**
 * Shared expected UI text contract used across Espresso tests and out-of-process UIAutomator tests.
 */
object UiText {

    object Common {
        const val OK = "OK"
    }

    object Home {
        const val TITLE_KEYWORD = "WEBDRIVER"
        const val SUBTITLE_PREFIX = "Demo app for"
    }

    object Login {
        const val LOGIN_SUCCESS_TITLE = "Success"
        const val SIGNUP_SUCCESS_TITLE = "Signed Up"
    }

    object Forms {
        const val INPUT_HINT = "Type something"
        const val ACTIVE_DIALOG_TITLE_PREFIX = "This button is"
        const val ACTIVE_DIALOG_MESSAGE = "This button is active"
        val ACTIVE_DIALOG_BUTTONS = listOf("Ask me later", "Cancel", Common.OK)
    }
}
