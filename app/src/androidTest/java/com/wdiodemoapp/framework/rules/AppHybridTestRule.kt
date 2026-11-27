package com.wdiodemoapp.framework.rules

import android.app.Activity
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.wdiodemoapp.MainActivity
import com.wdiodemoapp.testshared.FailureArtifactsRule
import com.wdiodemoapp.testshared.SystemActions
import org.junit.rules.ExternalResource
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * Combined JUnit rule managing ActivityScenario lifecycle, UIAutomator SystemActions,
 * and automatic screenshot/hierarchy/logcat capture on test failure.
 */
class AppHybridTestRule(
    private val launchIntent: Intent? = null,
) : TestRule {

    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    val device: UiDevice get() = UiDevice.getInstance(instrumentation)
    val system: SystemActions get() = SystemActions(device, instrumentation.targetContext)

    var scenario: ActivityScenario<MainActivity>? = null
        private set

    private val scenarioResource = object : ExternalResource() {
        override fun before() {
            scenario = if (launchIntent != null) {
                ActivityScenario.launch(launchIntent)
            } else {
                ActivityScenario.launch(MainActivity::class.java)
            }
        }

        override fun after() {
            scenario?.close()
            scenario = null
        }
    }

    private val chain: RuleChain = RuleChain
        .outerRule(scenarioResource)
        .around(FailureArtifactsRule())

    override fun apply(base: Statement, description: Description): Statement =
        chain.apply(base, description)

    fun <T : Activity> recreateCurrentActivity() {
        scenario?.recreate()
    }
}
