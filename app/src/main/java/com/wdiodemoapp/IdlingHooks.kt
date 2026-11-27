package com.wdiodemoapp

import androidx.test.espresso.idling.CountingIdlingResource

/**
 * Lightweight Espresso IdlingResource hooks so tests synchronize deterministically
 * with WebView page loads and ViewPager2 carousel settle animations without sleeps.
 */
object IdlingHooks {
    val webViewResource = CountingIdlingResource("WebViewPageLoad")
    val carouselResource = CountingIdlingResource("CarouselScroll")

    private var webViewBusy = false
    private var carouselBusy = false

    fun onWebViewLoading(loading: Boolean) {
        if (loading && !webViewBusy) {
            webViewBusy = true
            webViewResource.increment()
        } else if (!loading && webViewBusy) {
            webViewBusy = false
            webViewResource.decrement()
        }
    }

    fun onCarouselScrolling(scrolling: Boolean) {
        if (scrolling && !carouselBusy) {
            carouselBusy = true
            carouselResource.increment()
        } else if (!scrolling && carouselBusy) {
            carouselBusy = false
            carouselResource.decrement()
        }
    }
}
