package com.wdiodemoapp.testshared

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

private const val FONT_SCALE_TIMEOUT_MS = 5_000L

/**
 * Temporarily applies a system-wide Settings.System font scale via UIAutomator shell,
 * waits for Android Resources to reflect the scale, and restores the original value afterward.
 */
class FontScaleRule(private val scale: Float) : TestRule {

    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val device: UiDevice get() = UiDevice.getInstance(instrumentation)

    override fun apply(base: Statement, description: Description): Statement = object : Statement() {
        override fun evaluate() {
            val originalSetting = readSetting()
            val originalScale = instrumentation.targetContext.resources.configuration.fontScale
            var testError: Throwable? = null

            try {
                applyScale(scale.toString(), scale)
                base.evaluate()
            } catch (error: Throwable) {
                testError = error
                throw error
            } finally {
                try {
                    applyScale(originalSetting, originalScale)
                } catch (cleanupError: Throwable) {
                    if (testError == null) throw cleanupError
                    testError.addSuppressed(cleanupError)
                }
            }
        }
    }

    private fun readSetting(): String =
        device.executeShellCommand("settings get system font_scale").trim()

    private fun applyScale(setting: String, expected: Float) {
        val command = if (setting == "null") {
            "settings delete system font_scale"
        } else {
            require(setting.matches(Regex("[0-9]+(?:\\.[0-9]+)?"))) {
                "Invalid font scale setting: $setting"
            }
            "settings put system font_scale $setting"
        }
        device.executeShellCommand(command)

        Waiters.waitUntil(
            timeoutMs = FONT_SCALE_TIMEOUT_MS,
            pollIntervalMs = 100L,
            failureMessage = {
                "Expected font scale $expected ($setting), found " +
                    "${instrumentation.targetContext.resources.configuration.fontScale} (${readSetting()})"
            },
        ) {
            val currentScale = instrumentation.targetContext.resources.configuration.fontScale
            if (currentScale != expected) {
                false
            } else {
                val currentSetting = readSetting()
                currentSetting == setting ||
                    (setting == "null" && currentSetting.toFloatOrNull() == expected)
            }
        }
    }
}
