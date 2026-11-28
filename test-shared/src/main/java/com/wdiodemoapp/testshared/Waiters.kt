package com.wdiodemoapp.testshared

import android.os.SystemClock

/**
 * Shared polling utilities for deterministic UIAutomator, Espresso-Web, and system assertions.
 */
object Waiters {

    fun waitUntil(
        timeoutMs: Long = TestConfig.uiTimeoutMs,
        pollIntervalMs: Long = 100L,
        failureMessage: () -> String,
        condition: () -> Boolean,
    ) {
        poll(timeoutMs, pollIntervalMs, failureMessage) {
            if (condition()) null else ConditionNotMet
        }
    }

    fun waitUntilNoThrow(
        timeoutMs: Long = TestConfig.uiTimeoutMs,
        pollIntervalMs: Long = 200L,
        failureMessage: () -> String,
        block: () -> Unit,
    ) {
        poll(timeoutMs, pollIntervalMs, failureMessage) {
            try {
                block()
                null
            } catch (error: Throwable) {
                error
            }
        }
    }

    private fun poll(
        timeoutMs: Long,
        pollIntervalMs: Long,
        failureMessage: () -> String,
        attempt: () -> Throwable?,
    ) {
        val deadline = SystemClock.elapsedRealtime() + timeoutMs
        var lastError: Throwable?
        while (true) {
            lastError = attempt()
            if (lastError == null) return
            if (SystemClock.elapsedRealtime() >= deadline) {
                val message = failureMessage()
                if (lastError === ConditionNotMet) {
                    throw AssertionError(message)
                }
                throw AssertionError(message, lastError)
            }
            SystemClock.sleep(pollIntervalMs)
        }
    }

    private object ConditionNotMet : Throwable()
}
