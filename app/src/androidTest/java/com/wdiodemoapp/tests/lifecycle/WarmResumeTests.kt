package com.wdiodemoapp.tests.lifecycle

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.wdiodemoapp.MainActivity
import com.wdiodemoapp.framework.rules.AppHybridTestRule
import com.wdiodemoapp.framework.screens.FormsRobot
import com.wdiodemoapp.testshared.LifecycleTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Hybrid Espresso + UIAutomator lifecycle tests:
 * Espresso drives/verifies in-app Forms state, while UIAutomator performs OS Home/Recents actions.
 */
@LifecycleTest
@RunWith(AndroidJUnit4::class)
class WarmResumeTests {

    @get:Rule
    val app = AppHybridTestRule()

    @Test
    fun formsStateSurvivesHomeAndResume() {
        FormsRobot.open()
            .enterText("")
            .setSwitch(false)
            .assertStateCleared()
            .enterText("Draft before switching")
            .setSwitch(true)

        val initialPid = app.system.mainProcessId()
        app.system.sendToBackground()
        app.system.activateApp()

        FormsRobot.assertVisible()
            .assertInputText("Draft before switching")
            .assertSwitchChecked(true)
            .setSwitch(false)
            .assertSwitchChecked(false)

        assertEquals("Main process restarted during warm resume", initialPid, app.system.mainProcessId())
    }

    @Test
    fun formsStateSurvivesOpeningAndroidRecents() {
        FormsRobot.open()
            .enterText("Android draft")
            .setSwitch(true)

        val initialPid = app.system.mainProcessId()
        app.system.openRecents()
        app.system.activateApp()

        FormsRobot.assertVisible()
            .assertInputText("Android draft")
            .assertSwitchChecked(true)

        assertEquals("Main process restarted during Recents switch", initialPid, app.system.mainProcessId())
    }

    @Test
    fun activityRecreationClearsTemporaryFormsDraft() {
        FormsRobot.open()
            .enterText("Draft before recreate")
            .setSwitch(true)

        app.recreateCurrentActivity<MainActivity>()

        FormsRobot.open()
            .assertStateCleared()
    }
}
