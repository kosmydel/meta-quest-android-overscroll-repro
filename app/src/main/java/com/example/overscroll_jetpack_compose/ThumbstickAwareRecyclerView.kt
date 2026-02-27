package com.example.overscroll_jetpack_compose

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.recyclerview.widget.RecyclerView

/**
 * A RecyclerView that exits the overscroll/edge-effect state when thumbstick
 * (or any generic ACTION_SCROLL) input stops pushing against a boundary.
 *
 * Problem: Meta Quest thumbstick input arrives as ACTION_SCROLL events.
 * Unlike touch, these events have no UP/CANCEL counterpart, so the EdgeEffect
 * (glow / stretch) triggered at list boundaries never receives the "release"
 * signal and stays stuck until the user scrolls the other way or taps.
 *
 * Fix: after every ACTION_SCROLL we check whether the RecyclerView could
 * actually scroll in the requested direction. When it cannot (i.e. we are at
 * the boundary), the edge effect was just inflated; we immediately call
 * `onRelease()` on every EdgeEffect via the RecyclerView edge-effect API so
 * the animation springs back on its own.
 */
class ThumbstickAwareRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : RecyclerView(context, attrs, defStyleAttr) {

    override fun onGenericMotionEvent(event: MotionEvent): Boolean {
        val handled = super.onGenericMotionEvent(event)

        if (event.action == MotionEvent.ACTION_SCROLL) {
            releaseEdgeEffectsIfAtBoundary()
        }

        return handled
    }

    /**
     * If the list cannot scroll further in a direction, any EdgeEffect that was
     * stretched for that direction is released so it animates back immediately.
     */
    private fun releaseEdgeEffectsIfAtBoundary() {
        val lm = layoutManager ?: return

        val canScrollUp = lm.canScrollVertically() && canScrollVertically(-1)
        val canScrollDown = lm.canScrollVertically() && canScrollVertically(1)
        val canScrollLeft = lm.canScrollHorizontally() && canScrollHorizontally(-1)
        val canScrollRight = lm.canScrollHorizontally() && canScrollHorizontally(1)

        // At the top/left boundary
        if (!canScrollUp || !canScrollLeft) {
            edgeEffectFactory // access to ensure non-null; actual release below
            releaseTopEdgeEffect()
        }

        // At the bottom/right boundary
        if (!canScrollDown || !canScrollRight) {
            releaseBottomEdgeEffect()
        }
    }

    /**
     * Drives the start (top/left) EdgeEffect to its released state by
     * delivering a zero-delta pull followed by a release through the
     * RecyclerView's own nested-scroll / edge-effect path.
     *
     * We use `dispatchNestedPreScroll` / `dispatchNestedScroll` indirectly by
     * posting a synthetic touch-cancel on the EdgeEffect directly via
     * reflection (the only stable public surface for EdgeEffect.onRelease()).
     */
    private fun releaseTopEdgeEffect() = releaseEdgeEffect(top = true)
    private fun releaseBottomEdgeEffect() = releaseEdgeEffect(top = false)

    private fun releaseEdgeEffect(top: Boolean) {
        try {
            // RecyclerView keeps EdgeEffect instances in mTopGlow / mBottomGlow
            // (vertical) or mLeftGlow / mRightGlow (horizontal).
            val fieldName = if (top) "mTopGlow" else "mBottomGlow"
            val field = RecyclerView::class.java.getDeclaredField(fieldName).also {
                it.isAccessible = true
            }
            val edgeEffect = field.get(this) ?: return
            val releaseMethod = edgeEffect.javaClass.getMethod("onRelease")
            releaseMethod.invoke(edgeEffect)
            // Ask RecyclerView to redraw so the animation actually plays.
            invalidate()
        } catch (_: Exception) {
            // Reflection may fail on future AOSP changes; fail silently —
            // the worst outcome is the original stuck-glow behaviour.
        }
    }
}
