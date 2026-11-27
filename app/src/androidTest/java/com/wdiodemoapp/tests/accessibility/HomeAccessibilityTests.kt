package com.wdiodemoapp.tests.accessibility

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.wdiodemoapp.framework.rules.AppHybridTestRule
import com.wdiodemoapp.framework.screens.FormsRobot
import com.wdiodemoapp.framework.screens.HomeRobot
import com.wdiodemoapp.framework.screens.NavigationRobot
import com.wdiodemoapp.testshared.AccessibilityIds
import com.wdiodemoapp.testshared.AccessibilityTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@AccessibilityTest
@RunWith(AndroidJUnit4::class)
class HomeAccessibilityTests {

    @get:Rule
    val app = AppHybridTestRule()

    @Test
    fun homeExposesReadableContent() {
        HomeRobot.assertReadableText("WEBDRIVER", "Demo app for")
    }

    @Test
    fun clickableHomeControlsExposeAccessibleNames() {
        HomeRobot.assertAllClickableControlsNamed()
    }

    @Test
    fun allNavigationTabsHaveUsableTouchTargets() {
        HomeRobot.assertVisible()
        AccessibilityIds.Tabs.ALL.forEach { tabLabel ->
            NavigationRobot.assertTabUsableTouchTarget(tabLabel)
        }
    }

    @Test
    fun tabSelectionFollowsActiveScreen() {
        HomeRobot.assertVisible()
        NavigationRobot.assertTabSelected(AccessibilityIds.Tabs.HOME)

        FormsRobot.open()
        NavigationRobot.assertTabSelected(AccessibilityIds.Tabs.FORMS)

        HomeRobot.open()
        NavigationRobot.assertTabSelected(AccessibilityIds.Tabs.HOME)
    }
}
