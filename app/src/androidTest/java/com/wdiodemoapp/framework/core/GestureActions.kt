package com.wdiodemoapp.framework.core

import android.view.View
import androidx.test.espresso.Espresso.onIdle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import org.hamcrest.Matcher

/**
 * Reusable touch gesture helpers combining Espresso view coordinate lookup
 * with pixel-accurate UIAutomator pointer drags.
 */
object GestureActions {

    fun dragAndDrop(sourceContentDesc: String, targetContentDesc: String, steps: Int = 24) {
        val sourceCoords = IntArray(2)
        val targetCoords = IntArray(2)
        onView(withContentDescription(sourceContentDesc)).perform(readCenterCoordinates(sourceCoords))
        onView(withContentDescription(targetContentDesc)).perform(readCenterCoordinates(targetCoords))
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
            .drag(sourceCoords[0], sourceCoords[1], targetCoords[0], targetCoords[1], steps)
        onIdle()
    }

    private fun readCenterCoordinates(outCoords: IntArray): ViewAction = object : ViewAction {
        override fun getConstraints(): Matcher<View> = isDisplayed()
        override fun getDescription(): String = "read screen center coordinates"

        override fun perform(uiController: UiController, view: View) {
            val location = IntArray(2)
            view.getLocationOnScreen(location)
            outCoords[0] = location[0] + view.width / 2
            outCoords[1] = location[1] + view.height / 2
        }
    }
}
