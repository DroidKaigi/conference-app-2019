package io.github.droidkaigi.confsched2019.session.ui.widget

import android.content.Context
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.children
import androidx.core.widget.NestedScrollView
import io.github.droidkaigi.confsched2019.session.R

class SessionToolbarBehavior(
    private val context: Context,
    private val toolbar: Toolbar,
    private val sessionTitle: String
) : CoordinatorLayout.Behavior<LinearLayout>() {

    private var hasSetToolbarTitle = false
    private var textView: TextView? = null
    private var mIsAnimation = false

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: LinearLayout,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean = axes == ViewCompat.SCROLL_AXIS_VERTICAL

    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: LinearLayout,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        val nestedScrollView = if (child.childCount > 0) {
            (child.children.find { it is NestedScrollView } as? NestedScrollView)
        } else {
            null
        }
        performAnimation(nestedScrollView)
    }

    private fun performAnimation(nestedScrollView: NestedScrollView?) {
        if (!mIsAnimation) {
            if (nestedScrollView?.canScrollVertically(NEGATIVE_DIRECTION) == false) {
                textView?.let {
                    animateTitle(TOP_TITLE_ANIMATION_ALPHA, it)
                    toolbar.elevation = context.resources.getDimension(
                        R.dimen.session_detail_toolbar_elevation_top
                    ) / context.resources.displayMetrics.density
                }
            } else {
                setToolbarTitle()
                textView?.let {
                    animateTitle(NO_TOP_TITLE_ANIMATION_ALPHA, it)
                    toolbar.elevation = context.resources.getDimension(
                        R.dimen.session_detail_toolbar_elevation_not_top
                    ) / context.resources.displayMetrics.density
                }
            }
        }
    }

    private fun setToolbarTitle() {
        if (!hasSetToolbarTitle) {
            toolbar.title = sessionTitle
            textView = toolbar.children.find { it is TextView } as TextView
            hasSetToolbarTitle = true
        }
    }

    private fun animateTitle(
        alpha: Float,
        child: TextView
    ) {
        ViewCompat.animate(child)
            .alpha(alpha)
            .setDuration(TOOLBAR_TITLE_ANIMATION_DURATION_IN_MILLIS)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withStartAction {
                mIsAnimation = true
            }
            .withEndAction {
                mIsAnimation = false
            }
    }

    companion object {
        private const val NEGATIVE_DIRECTION = -1
        private const val TOOLBAR_TITLE_ANIMATION_DURATION_IN_MILLIS = 20L
        private const val TOP_TITLE_ANIMATION_ALPHA = 0f
        private const val NO_TOP_TITLE_ANIMATION_ALPHA = 1f
    }
}
