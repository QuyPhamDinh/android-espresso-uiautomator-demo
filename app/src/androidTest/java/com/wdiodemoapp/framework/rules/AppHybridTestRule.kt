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
 * Generic JUnit rule managing [ActivityScenario] lifecycle for any [Activity],
 * UIAutomator [SystemActions], and automatic screenshot/hierarchy/logcat capture on test failure.
 */
class AppHybridTestRule<A : Activity>(
    private val activityClass: Class<A>,
    private val intentProvider: (() -> Intent)? = null,
) : TestRule {

    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    val device: UiDevice get() = UiDevice.getInstance(instrumentation)
    val system: SystemActions get() = SystemActions(device, instrumentation.targetContext)

    var scenario: ActivityScenario<A>? = null
        private set

    private val scenarioResource = object : ExternalResource() {
        override fun before() {
            val intent = intentProvider?.invoke()
            scenario = if (intent != null) {
                ActivityScenario.launch(intent)
            } else {
                ActivityScenario.launch(activityClass)
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

    fun recreateCurrentActivity() {
        scenario?.recreate()
    }

    companion object {
        operator fun invoke(): AppHybridTestRule<MainActivity> =
            AppHybridTestRule(MainActivity::class.java)

        operator fun invoke(launchIntent: Intent): AppHybridTestRule<MainActivity> =
            AppHybridTestRule(MainActivity::class.java) { launchIntent }

        operator fun invoke(intentProvider: () -> Intent): AppHybridTestRule<MainActivity> =
            AppHybridTestRule(MainActivity::class.java, intentProvider)

        inline fun <reified A : Activity> forActivity(
            noinline intentProvider: (() -> Intent)? = null,
        ): AppHybridTestRule<A> = AppHybridTestRule(A::class.java, intentProvider)
    }
}
