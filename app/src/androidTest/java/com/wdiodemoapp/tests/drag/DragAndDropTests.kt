package com.wdiodemoapp.tests.drag

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.wdiodemoapp.framework.rules.AppHybridTestRule
import com.wdiodemoapp.framework.screens.DragRobot
import com.wdiodemoapp.testshared.RegressionTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RegressionTest
@RunWith(AndroidJUnit4::class)
class DragAndDropTests {

    @get:Rule
    val app = AppHybridTestRule()

    @Test
    fun solvesPuzzleAndResetsWithRetryButton() {
        DragRobot.open()
            .solvePuzzle()
            .assertRetryVisible()
            .tapRetry()
            .assertRenewVisible()
    }

    @Test
    fun renewButtonResetsInProgressPuzzle() {
        DragRobot.open()
            .assertRenewVisible()
            .tapRenew()
            .assertRenewVisible()
    }
}
