package com.wdiodemoapp.tests.navigation

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.wdiodemoapp.framework.rules.AppHybridTestRule
import com.wdiodemoapp.framework.screens.DragRobot
import com.wdiodemoapp.framework.screens.FormsRobot
import com.wdiodemoapp.framework.screens.HomeRobot
import com.wdiodemoapp.framework.screens.LoginRobot
import com.wdiodemoapp.framework.screens.NavigationRobot
import com.wdiodemoapp.framework.screens.SwipeRobot
import com.wdiodemoapp.framework.screens.WebViewRobot
import com.wdiodemoapp.testshared.SmokeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@SmokeTest
@RunWith(AndroidJUnit4::class)
class TabBarNavigationTests {

    @get:Rule
    val app = AppHybridTestRule()

    @Test
    fun tabBarNavigatesAcrossAllSixScreens() {
        NavigationRobot.assertTabBarReady()
        HomeRobot.assertVisible()

        WebViewRobot.open()
        LoginRobot.open()
        FormsRobot.open()
        SwipeRobot.open()
        DragRobot.open()
        HomeRobot.open()
    }
}
