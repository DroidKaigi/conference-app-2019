package io.github.droidkaigi.confsched2019.session.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorListener
import androidx.core.view.children
import androidx.core.widget.NestedScrollView
import io.github.droidkaigi.confsched2019.session.R

class SessionToolbarBehavior(
    val context: Context? = null,
    attrs: AttributeSet? = null
) : CoordinatorLayout.Behavior<LinearLayout>(context, attrs) {

    private var sessionTitle: String = ""
    private var hasSetToolbarTitle = false
    private var appCompatTextView: AppCompatTextView? = null

    private var mIsAnimation = false

    constructor(context: Context?, attrs: AttributeSet?, sessionTitle: String) : this(
        context,
        attrs
    ) {
        this.sessionTitle = sessionTitle
    }

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
        val toolbar = child.findViewById<Toolbar>(R.id.session_toolbar)
        val nestedScrollView = if (child.childCount > 0) {
            (child.children.find { it is NestedScrollView } as? NestedScrollView)
        } else {
            null
        }
        performAnimation(toolbar, nestedScrollView)
    }

    private fun performAnimation(toolbar: Toolbar, nestedScrollView: NestedScrollView?) {
        if (!mIsAnimation) {
            if (nestedScrollView?.canScrollVertically(NEGATIVE_DIRECTION) == false) {
                appCompatTextView?.let {
                    animateTitle(TOP_TITLE_ANIMATION_ALPHA, TOP_TITLE_ANIMATION_DURATION, it)
                    animateToolbarElevation(toolbar, TOP_TOOLBAR_ELEVATION)
                }
            } else {
                initToolbarTitle(toolbar)
                appCompatTextView?.let {
                    animateTitle(NO_TOP_TITLE_ANIMATION_ALPHA, NO_TOP_TITLE_ANIMATION_DURATION, it)
                    animateToolbarElevation(toolbar, NO_TOP_TOOLBAR_ELEVATION)
                }
            }
        }
    }

    private fun initToolbarTitle(toolbar: Toolbar) {
        if (!hasSetToolbarTitle) {
            toolbar.title = sessionTitle
            appCompatTextView =
                toolbar.children.find { it is AppCompatTextView } as AppCompatTextView
            hasSetToolbarTitle = true
        }
    }

    private fun animateToolbarElevation(toolbar: Toolbar, dp: Float) {
        val floatDp = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp,
            context?.resources?.displayMetrics
        )
        toolbar.elevation = floatDp
    }

    private fun animateTitle(
        alpha: Float,
        duration: Long,
        child: AppCompatTextView
    ) {
        ViewCompat.animate(child)
            .alpha(alpha)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setListener(object : ViewPropertyAnimatorListener {
                override fun onAnimationEnd(view: View?) {
                    mIsAnimation = false
                }

                override fun onAnimationCancel(view: View?) {
                    mIsAnimation = false
                }

                override fun onAnimationStart(view: View?) {
                    mIsAnimation = true
                }
            }).duration = duration
    }

    companion object {
        private const val NEGATIVE_DIRECTION = -1

        private const val TOP_TITLE_ANIMATION_ALPHA = 0f
        private const val TOP_TITLE_ANIMATION_DURATION = 20L
        private const val TOP_TOOLBAR_ELEVATION = 0f

        private const val NO_TOP_TITLE_ANIMATION_ALPHA = 1f
        private const val NO_TOP_TITLE_ANIMATION_DURATION = 20L
        private const val NO_TOP_TOOLBAR_ELEVATION = 4f
    }
}
