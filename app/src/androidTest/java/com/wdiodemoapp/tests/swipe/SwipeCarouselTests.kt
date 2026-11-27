package com.wdiodemoapp.tests.swipe

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.wdiodemoapp.IdlingHooks
import com.wdiodemoapp.framework.core.IdlingResourceRule
import com.wdiodemoapp.framework.rules.AppHybridTestRule
import com.wdiodemoapp.framework.screens.SwipeRobot
import com.wdiodemoapp.testshared.AccessibilityIds
import com.wdiodemoapp.testshared.RegressionTest
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@RegressionTest
@RunWith(AndroidJUnit4::class)
class SwipeCarouselTests {

    @get:Rule
    val rules: RuleChain = RuleChain
        .outerRule(IdlingResourceRule(IdlingHooks.carouselResource))
        .around(AppHybridTestRule())

    @Test
    fun carouselSwipesForwardAndBackwardAcrossAllCards() {
        SwipeRobot.open()
            .assertCardDisplayed(0)

        for (nextIndex in 1 until AccessibilityIds.Swipe.CARDS.size) {
            SwipeRobot.swipeLeft()
                .assertCardDisplayed(nextIndex)
        }

        for (prevIndex in AccessibilityIds.Swipe.CARDS.size - 2 downTo 0) {
            SwipeRobot.swipeRight()
                .assertCardDisplayed(prevIndex)
        }
    }

    @Test
    fun verticalScrollRevealsSurpriseLogo() {
        SwipeRobot.open()
            .scrollToSurpriseLogo()
    }
}
