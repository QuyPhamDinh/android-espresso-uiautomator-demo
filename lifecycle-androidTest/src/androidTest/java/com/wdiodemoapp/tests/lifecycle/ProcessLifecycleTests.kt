package com.wdiodemoapp.tests.lifecycle

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.wdiodemoapp.testshared.FailureArtifactsRule
import com.wdiodemoapp.testshared.FormsScreen
import com.wdiodemoapp.testshared.LifecycleTest
import com.wdiodemoapp.testshared.SystemActions
import com.wdiodemoapp.testshared.UiAutomatorFormsRobot
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * External black-box UIAutomator lifecycle suite executed under `:lifecycle-androidTest`
 * (`com.wdiodemoapp.lifecycle.host`) so `am kill` and `am force-stop` on `com.wdiodemoapp`
 * never terminate the test runner itself.
 */
@LifecycleTest
@RunWith(AndroidJUnit4::class)
class ProcessLifecycleTests {

    @get:Rule
    val artifacts = FailureArtifactsRule()

    private lateinit var system: SystemActions
    private lateinit var forms: FormsScreen

    @Before
    fun setUp() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val device = UiDevice.getInstance(instrumentation)
        system = SystemActions(device, instrumentation.targetContext)
        forms = UiAutomatorFormsRobot(device)
        system.forceStop()
        system.activateApp()
    }

    @Test
    fun processRecreationClearsTemporaryFormsState() {
        forms.open()
            .enterText("Draft before kill")
            .setSwitch(true)

        system.sendToBackground()
        system.killBackgroundProcess()
        system.activateApp()

        forms.open()
            .assertStateCleared()
    }

    @Test
    fun forceStopClearsTemporaryFormsState() {
        forms.open()
            .enterText("Draft before termination")
            .setSwitch(true)

        system.forceStop()
        system.activateApp()

        forms.open()
            .assertStateCleared()
    }
}
