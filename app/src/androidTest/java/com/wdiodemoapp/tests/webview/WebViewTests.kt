package com.wdiodemoapp.tests.webview

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.wdiodemoapp.IdlingHooks
import com.wdiodemoapp.framework.core.IdlingResourceRule
import com.wdiodemoapp.framework.rules.AppHybridTestRule
import com.wdiodemoapp.framework.screens.SwipeRobot
import com.wdiodemoapp.framework.screens.WebViewRobot
import com.wdiodemoapp.testshared.RegressionTest
import com.wdiodemoapp.testshared.WebViewTest
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@WebViewTest
@RegressionTest
@RunWith(AndroidJUnit4::class)
class WebViewTests {

    @get:Rule
    val rules: RuleChain = RuleChain
        .outerRule(IdlingResourceRule(IdlingHooks.webViewResource))
        .around(AppHybridTestRule())

    @Test
    fun webViewSearchOpensUrlDocumentation() {
        WebViewRobot.open()
            .openDocSearch()
            .typeDocSearchQuery("url")
            .clickCss("#docsearch-hits0-item-0 a")
            .assertHeadingContains("header h1", "url")
    }

    @Test
    fun switchesCleanlyBetweenWebViewNativeAndWebView() {
        WebViewRobot.open()
            .openDocSearch()

        SwipeRobot.open()

        WebViewRobot.open()
            .typeDocSearchQuery("OCR Testing")
            .clickCss(".DocSearch-Hit a[href*=\"/docs/ocr-testing/ocr-testing\"]")
            .assertHeadingContains("header h1", "OCR Testing")
    }
}
