package com.wdiodemoapp.framework.screens

import android.os.SystemClock
import android.webkit.WebView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.web.assertion.WebViewAssertions.webMatches
import androidx.test.espresso.web.model.Atoms.scriptWithArgs
import androidx.test.espresso.web.sugar.Web.onWebView
import androidx.test.espresso.web.webdriver.DriverAtoms.findElement
import androidx.test.espresso.web.webdriver.DriverAtoms.getText
import androidx.test.espresso.web.webdriver.DriverAtoms.webClick
import androidx.test.espresso.web.webdriver.Locator
import com.wdiodemoapp.testshared.TestConfig
import org.hamcrest.Matchers.containsString

/**
 * Robot for WebView native display checks and Espresso-Web DOM interactions.
 */
object WebViewRobot {

    fun open() = apply {
        NavigationRobot.openWebView()
        assertWebViewDisplayed()
    }

    fun assertWebViewDisplayed() = apply {
        onView(isAssignableFrom(WebView::class.java)).check(matches(isDisplayed()))
    }

    fun openDocSearch() = apply {
        waitForCssElement(".DocSearch")
        clickCss(".DocSearch")
        waitForCssElement(".DocSearch-Input")
    }

    fun ensureDocSearchOpen() = apply {
        val isInputPresent = runCatching {
            onWebView().withElement(findElement(Locator.CSS_SELECTOR, ".DocSearch-Input"))
        }.isSuccess
        if (!isInputPresent) {
            openDocSearch()
        }
    }

    fun typeDocSearchQuery(query: String) = apply {
        ensureDocSearchOpen()
        waitForCssElement(".DocSearch-Input")
        onWebView()
            .withElement(findElement(Locator.CSS_SELECTOR, ".DocSearch-Input"))
            .perform(
                scriptWithArgs(
                    """
                    var el = arguments[0];
                    var text = arguments[1];
                    el.focus();
                    var descriptor = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value');
                    if (descriptor && descriptor.set) {
                        descriptor.set.call(el, text);
                    } else {
                        el.value = text;
                    }
                    el.dispatchEvent(new Event('input', { bubbles: true }));
                    el.dispatchEvent(new Event('change', { bubbles: true }));
                    """.trimIndent(),
                    listOf(query),
                ),
            )
    }

    fun clickCss(cssSelector: String, timeoutMs: Long = TestConfig.uiTimeoutMs * 2) = apply {
        waitForCssElement(cssSelector, timeoutMs = timeoutMs)
        onWebView()
            .withElement(findElement(Locator.CSS_SELECTOR, cssSelector))
            .perform(webClick())
    }

    fun assertHeadingContains(
        cssSelector: String,
        expected: String,
        timeoutMs: Long = TestConfig.uiTimeoutMs * 2,
    ) = apply {
        val deadline = SystemClock.elapsedRealtime() + timeoutMs
        var lastError: Throwable? = null
        while (SystemClock.elapsedRealtime() < deadline) {
            try {
                onWebView()
                    .withElement(findElement(Locator.CSS_SELECTOR, cssSelector))
                    .check(webMatches(getText(), containsString(expected)))
                return@apply
            } catch (error: Throwable) {
                lastError = error
                SystemClock.sleep(200)
            }
        }
        throw AssertionError(
            "Timed out after ${timeoutMs}ms waiting for '$cssSelector' to contain '$expected'",
            lastError,
        )
    }

    fun waitForCssElement(cssSelector: String, timeoutMs: Long = TestConfig.uiTimeoutMs) = apply {
        val deadline = SystemClock.elapsedRealtime() + timeoutMs
        var lastError: Throwable? = null
        while (SystemClock.elapsedRealtime() < deadline) {
            try {
                onWebView().withElement(findElement(Locator.CSS_SELECTOR, cssSelector))
                return@apply
            } catch (error: Throwable) {
                lastError = error
                SystemClock.sleep(200)
            }
        }
        throw AssertionError(
            "Timed out after ${timeoutMs}ms waiting for CSS element '$cssSelector'",
            lastError,
        )
    }
}
