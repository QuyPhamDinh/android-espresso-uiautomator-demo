package com.wdiodemoapp.testshared

import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import java.io.File
import java.util.UUID
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Captures device screenshot, UIAutomator hierarchy XML, and logcat tail on test failure.
 */
class FailureArtifactsRule : TestWatcher() {

    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val device: UiDevice get() = UiDevice.getInstance(instrumentation)

    override fun failed(e: Throwable, description: Description) {
        runCatching {
            val context = instrumentation.targetContext
            val outDirArg = InstrumentationRegistry.getArguments().getString("additionalTestOutputDir")
            val root = outDirArg?.let(::File)
                ?: context.getExternalFilesDir(null)
                ?: context.filesDir
            val targetDir = File(
                root,
                "failures/$runId/${description.className}/${description.methodName}",
            )
            if (!targetDir.isDirectory && !targetDir.mkdirs()) return

            runCatching { device.takeScreenshot(File(targetDir, "screen.png")) }
                .onFailure { Log.w(TAG, "Screenshot capture failed", it) }

            runCatching { device.dumpWindowHierarchy(File(targetDir, "hierarchy.xml")) }
                .onFailure { Log.w(TAG, "Hierarchy dump failed", it) }

            runCatching {
                File(targetDir, "logcat.txt")
                    .writeText(device.executeShellCommand("logcat -d -t 500"))
            }.onFailure { Log.w(TAG, "Logcat capture failed", it) }
        }.onFailure { Log.w(TAG, "Failure artifact collection failed", it) }
    }

    private companion object {
        const val TAG = "FailureArtifactsRule"
        val runId: String by lazy {
            InstrumentationRegistry.getArguments().getString("artifactRunId")
                ?.replace(Regex("[^a-zA-Z0-9._-]"), "_")
                ?: UUID.randomUUID().toString()
        }
    }
}
