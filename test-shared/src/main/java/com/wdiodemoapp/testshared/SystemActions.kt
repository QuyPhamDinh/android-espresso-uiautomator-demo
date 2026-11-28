package com.wdiodemoapp.testshared

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until

/**
 * UIAutomator wrapper for out-of-app system actions (Home, Recents, app activation,
 * deep links, shell process inspection, force-stop, and background process death).
 */
class SystemActions(
    private val device: UiDevice,
    private val context: Context,
    private val packageName: String = TestConfig.packageName,
) {
    private val appWindow = By.pkg(packageName).depth(0)

    fun activateApp() {
        val intent = checkNotNull(context.packageManager.getLaunchIntentForPackage(packageName)) {
            "App package '$packageName' is not installed on the device"
        }.apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
        awaitWindowVisible()
    }

    fun openDeepLink(route: String) {
        val uri = Uri.parse("${TestConfig.deepLinkScheme}$route")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage(packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
        awaitWindowVisible()
    }

    fun sendToBackground() {
        check(device.pressHome()) { "Failed to press device Home button" }
        awaitWindowGone()
    }

    fun openRecents() {
        check(device.pressRecentApps()) { "Failed to open Android Recents" }
        awaitWindowGone()
    }

    fun mainProcessId(): String =
        checkNotNull(processIds(includeSecondary = false).singleOrNull()) {
            "Expected exactly one main process for $packageName"
        }

    fun forceStop() {
        device.executeShellCommand("am force-stop $packageName")
        awaitWindowGone()
        check(processIds().isEmpty()) { "$packageName still running after force-stop" }
    }

    fun killBackgroundProcess() {
        awaitWindowGone()
        val initialPids = processIds()
        check(initialPids.isNotEmpty()) { "$packageName exited before am kill" }

        Waiters.waitUntil(
            timeoutMs = TestConfig.processKillTimeoutMs,
            pollIntervalMs = 500L,
            failureMessage = {
                "am kill did not stop $packageName (initial=$initialPids, remaining=${processIds()})"
            },
        ) {
            device.executeShellCommand("am kill $packageName")
            processIds().isEmpty()
        }
    }

    fun awaitWindowVisible() {
        check(device.wait(Until.hasObject(appWindow), TestConfig.uiTimeoutMs) == true) {
            "$packageName window did not become visible within ${TestConfig.uiTimeoutMs}ms"
        }
    }

    fun awaitWindowGone() {
        check(device.wait(Until.gone(appWindow), TestConfig.uiTimeoutMs) == true) {
            "$packageName window remained visible after ${TestConfig.uiTimeoutMs}ms"
        }
    }

    private fun processIds(includeSecondary: Boolean = true): List<String> =
        device.executeShellCommand("ps -A -o PID,NAME")
            .lineSequence()
            .map { it.trim().split(Regex("\\s+"), limit = 2) }
            .filter { parts ->
                parts.size == 2 &&
                    (parts[1] == packageName || (includeSecondary && parts[1].startsWith("$packageName:")))
            }
            .map { it[0] }
            .toList()
}
