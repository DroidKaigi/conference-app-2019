package io.github.droidkaigi.confsched2019.session.ui.widget

import android.content.Context
import android.util.AttributeSet
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
    context: Context? = null,
    attrs: AttributeSet? = null
) : CoordinatorLayout.Behavior<LinearLayout>(context, attrs) {

    var sessionTitle: String = ""
    var hasSetToolbarTitle = false
    var appCompatTextView: AppCompatTextView? = null

    var mIsAnimation = false

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
        if (!mIsAnimation) {
            if (nestedScrollView?.canScrollVertically(NEGATIVE_DIRECTION) == false) {
                appCompatTextView?.let {
                    animate(0f, 50, it)
                }
            } else {
                initToolbarTitle(toolbar)
                appCompatTextView?.let {
                    animate(1f, 50, it)
                }
            }
        }
    }

    private fun initToolbarTitle(toolbar: Toolbar) {
        if (!hasSetToolbarTitle) {
            toolbar.title = sessionTitle
            appCompatTextView =
                toolbar.children.first { it is AppCompatTextView } as AppCompatTextView
            hasSetToolbarTitle = true
        }
    }

    private fun animate(
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
                }

                override fun onAnimationStart(view: View?) {
                    mIsAnimation = true
                }
            }).duration = duration
    }

    companion object {
        private const val NEGATIVE_DIRECTION = -1
    }
}
