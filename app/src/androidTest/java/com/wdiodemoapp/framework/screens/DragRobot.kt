package com.wdiodemoapp.framework.screens

import com.wdiodemoapp.framework.core.EspressoActions.assertDisplayed
import com.wdiodemoapp.framework.core.EspressoActions.tapDesc
import com.wdiodemoapp.framework.core.GestureActions.dragAndDrop
import com.wdiodemoapp.testshared.AccessibilityIds

/**
 * Robot for the 3x3 Drag-and-Drop puzzle screen.
 */
object DragRobot {

    fun open() = apply {
        NavigationRobot.openDrag()
        assertVisible()
    }

    fun assertVisible() = apply {
        assertDisplayed(AccessibilityIds.Screens.DRAG)
    }

    fun solvePuzzle() = apply {
        AccessibilityIds.Drag.PUZZLE_PAIRS.forEach { (sourceDesc, targetDesc) ->
            dragAndDrop(sourceDesc, targetDesc)
        }
    }

    fun assertRetryVisible() = apply {
        assertDisplayed(AccessibilityIds.Drag.RETRY)
    }

    fun tapRetry() = apply {
        tapDesc(AccessibilityIds.Drag.RETRY)
    }

    fun assertRenewVisible() = apply {
        assertDisplayed(AccessibilityIds.Drag.RENEW)
    }

    fun tapRenew() = apply {
        tapDesc(AccessibilityIds.Drag.RENEW)
    }
}
