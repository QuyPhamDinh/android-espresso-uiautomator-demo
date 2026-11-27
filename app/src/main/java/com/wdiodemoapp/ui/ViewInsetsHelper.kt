package com.wdiodemoapp.ui

import android.view.View
import androidx.core.view.doOnLayout
import androidx.core.view.updatePadding

/**
 * Shifts top-aligned screen content down by ~1/3 of the available height once on initial layout
 * so top input/search fields stay clear of camera cutouts and top system areas without jumping
 * when the soft keyboard resizes the window.
 */
fun applyTopThirdOffset(
    container: View,
    target: View,
    fraction: Float = 1f / 3f,
    reservedContentHeightDp: Int = 0,
) {
    val basePaddingTop = target.paddingTop
    var applied = false
    container.doOnLayout { view ->
        if (applied || view.height <= 0) return@doOnLayout
        applied = true

        val desiredOffset = (view.height * fraction).toInt()
        val finalOffset = if (reservedContentHeightDp > 0) {
            val reservedPx = (reservedContentHeightDp * view.resources.displayMetrics.density).toInt()
            minOf(desiredOffset, maxOf(0, view.height - reservedPx))
        } else {
            desiredOffset
        }
        target.updatePadding(top = basePaddingTop + finalOffset)
    }
}
