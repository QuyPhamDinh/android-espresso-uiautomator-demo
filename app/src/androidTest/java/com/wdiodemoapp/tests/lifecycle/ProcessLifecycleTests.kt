package com.wdiodemoapp.tests.lifecycle

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import com.wdiodemoapp.testshared.AccessibilityIds
import com.wdiodemoapp.testshared.FailureArtifactsRule
import com.wdiodemoapp.testshared.LifecycleTest
import com.wdiodemoapp.testshared.SystemActions
import com.wdiodemoapp.testshared.TestConfig
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * External black-box UIAutomator lifecycle suite compiled and executed by `:lifecycle-androidTest`.
 * Stored alongside other lifecycle tests in `:app/src/androidTest` for unified test organization,
 * while running under `com.wdiodemoapp.lifecycle.host` so `am kill` and `am force-stop`
 * on `com.wdiodemoapp` never terminate the test runner itself.
 */
@LifecycleTest
@RunWith(AndroidJUnit4::class)
class ProcessLifecycleTests {

    @get:Rule
    val artifacts = FailureArtifactsRule()

    private lateinit var device: UiDevice
    private lateinit var system: SystemActions

    @Before
    fun setUp() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        device = UiDevice.getInstance(instrumentation)
        system = SystemActions(device, instrumentation.targetContext)
        system.forceStop()
        system.activateApp()
    }

    @Test
    fun processRecreationClearsTemporaryFormsState() {
        openFormsAndFillDraft("Draft before kill")

        system.sendToBackground()
        system.killBackgroundProcess()
        system.activateApp()

        openForms()
        assertFormsStateCleared()
    }

    @Test
    fun forceStopClearsTemporaryFormsState() {
        openFormsAndFillDraft("Draft before termination")

        system.forceStop()
        system.activateApp()

        openForms()
        assertFormsStateCleared()
    }

    private fun openForms() {
        waitFor(AccessibilityIds.Tabs.FORMS).click()
        waitFor(AccessibilityIds.Screens.FORMS)
    }

    private fun openFormsAndFillDraft(text: String) {
        openForms()
        val input = waitFor(AccessibilityIds.Forms.TEXT_INPUT)
        input.text = text
        val switchToggle = waitFor(AccessibilityIds.Forms.SWITCH)
        if (!switchToggle.isChecked) {
            switchToggle.click()
        }
        assertTrue(
            "Expected Forms switch to be enabled",
            device.wait(
                Until.findObject(descSelector(AccessibilityIds.Forms.SWITCH).checked(true)),
                TestConfig.uiTimeoutMs,
            ) != null,
        )
    }

    private fun assertFormsStateCleared() {
        val input = waitFor(AccessibilityIds.Forms.TEXT_INPUT)
        val inputText = input.text.orEmpty()
        assertTrue(
            "Expected cleared/hint text after restart, found '$inputText'",
            inputText.isEmpty() || inputText == "Type something",
        )
        val switchToggle = waitFor(AccessibilityIds.Forms.SWITCH)
        assertFalse("Expected switch to be off after restart", switchToggle.isChecked)
    }

    private fun waitFor(contentDesc: String): UiObject2 =
        checkNotNull(device.wait(Until.findObject(descSelector(contentDesc)), TestConfig.uiTimeoutMs)) {
            "Element with content-desc='$contentDesc' did not appear in ${TestConfig.packageName}"
        }

    private fun descSelector(contentDesc: String) =
        By.desc(contentDesc).pkg(TestConfig.packageName)
}
