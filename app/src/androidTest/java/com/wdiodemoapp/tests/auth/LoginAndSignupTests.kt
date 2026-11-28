package com.wdiodemoapp.tests.auth

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.wdiodemoapp.framework.rules.AppHybridTestRule
import com.wdiodemoapp.framework.screens.AlertRobot
import com.wdiodemoapp.framework.screens.LoginRobot
import com.wdiodemoapp.testshared.SmokeTest
import com.wdiodemoapp.testshared.TestConfig
import com.wdiodemoapp.testshared.UiText
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@SmokeTest
@RunWith(AndroidJUnit4::class)
class LoginAndSignupTests {

    @get:Rule
    val app = AppHybridTestRule()

    @Test
    fun successfulLoginShowsSuccessAlertAndDismisses() {
        LoginRobot.open()
            .login(TestConfig.TEST_EMAIL, TestConfig.TEST_PASSWORD)

        AlertRobot.assertTitleOrMessageContains(UiText.Login.LOGIN_SUCCESS_TITLE)
            .tapButton(UiText.Common.OK)
            .assertNoDialogWithText(UiText.Login.LOGIN_SUCCESS_TITLE)
    }

    @Test
    fun successfulSignupShowsSignedUpAlertAndDismisses() {
        LoginRobot.open()
            .signUp(TestConfig.TEST_EMAIL, TestConfig.TEST_PASSWORD)

        AlertRobot.assertTitleOrMessageContains(UiText.Login.SIGNUP_SUCCESS_TITLE)
            .tapButton(UiText.Common.OK)
            .assertNoDialogWithText(UiText.Login.SIGNUP_SUCCESS_TITLE)
    }
}
