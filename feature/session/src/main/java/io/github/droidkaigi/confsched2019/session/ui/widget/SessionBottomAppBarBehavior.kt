package io.github.droidkaigi.confsched2019.session.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.children
import androidx.core.widget.NestedScrollView
import com.google.android.material.bottomappbar.BottomAppBar

class SessionBottomAppBarBehavior(
    context: Context? = null,
    attrs: AttributeSet? = null
) : BottomAppBar.Behavior(context, attrs) {

    private var state: Int = STATE_SCROLLED_UP

    override fun onInterceptTouchEvent(
        parent: CoordinatorLayout,
        child: BottomAppBar,
        ev: MotionEvent
    ): Boolean {
        if (!child.isShown || ev.action != MotionEvent.ACTION_DOWN) {
            return super.onInterceptTouchEvent(parent, child, ev)
        }

        // If screen be able not to scrolled, it makes to slide the bottom app bar.
        if (!canScroll(parent)) {
            state = if (state == STATE_SCROLLED_UP) {
                super.slideDown(child)
                STATE_SCROLLED_DOWN
            } else {
                super.slideUp(child)
                STATE_SCROLLED_UP
            }
        }
        return super.onInterceptTouchEvent(parent, child, ev)
    }

    private fun canScroll(parent: CoordinatorLayout): Boolean {
        val nestedScrollView = if (parent.childCount > 0) {
            (parent.children.find { it is NestedScrollView } as? NestedScrollView)
        } else {
            null
        }
        return nestedScrollView?.canScrollVertically(POSITIVE_DIRECTION) ?: true ||
            nestedScrollView?.canScrollVertically(NEGATIVE_DIRECTION) ?: true
    }

    companion object {
        private const val POSITIVE_DIRECTION = 1
        private const val NEGATIVE_DIRECTION = -1

        private const val STATE_SCROLLED_DOWN = 1
        private const val STATE_SCROLLED_UP = 2
    }
}
