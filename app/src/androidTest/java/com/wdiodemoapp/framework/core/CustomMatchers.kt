package com.wdiodemoapp.framework.core

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.util.TreeIterables
import java.util.Locale
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.Assert.assertTrue

/**
 * Reusable Espresso matchers and accessibility ViewAssertions.
 */
object CustomMatchers {

    fun hasMinimumTouchTargetDp(minDp: Float = 47.5f): Matcher<View> =
        object : BoundedMatcher<View, View>(View::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("touch target size at least ${minDp}x${minDp} dp")
            }

            override fun matchesSafely(item: View): Boolean {
                val density = item.resources.displayMetrics.density
                val widthDp = item.width / density
                val heightDp = item.height / density
                return widthDp >= minDp && heightDp >= minDp
            }

            override fun describeMismatch(item: Any?, mismatchDescription: Description) {
                val view = item as? View
                if (view == null) {
                    super.describeMismatch(item, mismatchDescription)
                    return
                }
                val density = view.resources.displayMetrics.density
                mismatchDescription.appendText(
                    String.format(
                        Locale.ROOT,
                        "was %.1f x %.1f dp",
                        view.width / density,
                        view.height / density,
                    ),
                )
            }
        }

    fun clickableControlsHaveAccessibleNames(): ViewAssertion = ViewAssertion { root, noViewFoundException ->
        if (noViewFoundException != null) throw noViewFoundException
        val clickableControls = TreeIterables.breadthFirstViewTraversal(root)
            .filter { it.isShown && it.isClickable }

        assertTrue("Expected at least one clickable control on screen", clickableControls.isNotEmpty())

        val unnamed = clickableControls.filterNot { it.hasAccessibleName() }
            .map { "${it.javaClass.simpleName}(id=${it.id})" }

        assertTrue(
            "Found clickable controls without accessible names: ${unnamed.joinToString()}",
            unnamed.isEmpty(),
        )
    }

    private fun View.hasAccessibleName(): Boolean {
        if (!contentDescription.isNullOrBlank()) return true
        if (this is TextView && !text.isNullOrBlank()) return true
        if (this is ViewGroup) {
            for (index in 0 until childCount) {
                val child = getChildAt(index)
                if (child.isShown && !child.isClickable && child.hasAccessibleName()) {
                    return true
                }
            }
        }
        return false
    }
}
