package com.wdiodemoapp.testshared

import androidx.test.platform.app.InstrumentationRegistry

/**
 * Runtime test configuration with optional InstrumentationRegistry overrides.
 */
object TestConfig {
    private fun arg(key: String, default: String): String =
        InstrumentationRegistry.getArguments().getString(key)?.takeIf { it.isNotBlank() } ?: default

    val packageName: String get() = arg("targetPackage", "com.wdiodemoapp")
    val deepLinkScheme: String get() = arg("deepLinkScheme", "wdio://")

    val uiTimeoutMs: Long = 10_000L
    val dialogTimeoutMs: Long = 11_000L
    val processKillTimeoutMs: Long = 12_000L

    const val TEST_EMAIL = "test@webdriver.io"
    const val TEST_PASSWORD = "Test1234!"
}
