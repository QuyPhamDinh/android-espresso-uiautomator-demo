package com.wdiodemoapp.tests.navigation

import android.content.Intent
import android.net.Uri
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.wdiodemoapp.MainActivity
import com.wdiodemoapp.framework.screens.DragRobot
import com.wdiodemoapp.framework.screens.FormsRobot
import com.wdiodemoapp.framework.screens.HomeRobot
import com.wdiodemoapp.framework.screens.LoginRobot
import com.wdiodemoapp.framework.screens.SwipeRobot
import com.wdiodemoapp.framework.screens.WebViewRobot
import com.wdiodemoapp.testshared.FailureArtifactsRule
import com.wdiodemoapp.testshared.SmokeTest
import com.wdiodemoapp.testshared.TestConfig
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@SmokeTest
@RunWith(AndroidJUnit4::class)
class DeepLinkNavigationTests {

    @get:Rule
    val artifacts = FailureArtifactsRule()

    @Test
    fun deepLinksOpenAllTargetScreens() {
        launchRoute("webview") { WebViewRobot.assertWebViewDisplayed() }
        launchRoute("login") { LoginRobot.assertVisible() }
        launchRoute("forms") { FormsRobot.assertVisible() }
        launchRoute("swipe") { SwipeRobot.assertVisible() }
        launchRoute("drag") { DragRobot.assertVisible() }
        launchRoute("") { HomeRobot.assertVisible() }
    }

    private fun launchRoute(route: String, verify: () -> Unit) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("${TestConfig.deepLinkScheme}$route"),
            ApplicationProvider.getApplicationContext(),
            MainActivity::class.java,
        )
        ActivityScenario.launch<MainActivity>(intent).use {
            verify()
        }
    }
}
