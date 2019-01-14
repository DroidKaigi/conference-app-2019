package io.github.droidkaigi.confsched2019.session.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import io.github.droidkaigi.confsched2019.session.R

class SessionToolbarBehavior(
    context: Context? = null,
    attrs: AttributeSet? = null
) : CoordinatorLayout.Behavior<LinearLayout>(context, attrs) {

    var sessionTitle: String = ""

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
    ): Boolean {
        val toolbar = child.findViewById<Toolbar>(R.id.session_toolbar)
        toolbar.title = sessionTitle
        return super.onStartNestedScroll(
            coordinatorLayout,
            child,
            directTargetChild,
            target,
            axes,
            type
        )
    }

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: LinearLayout,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {
        super.onNestedScroll(
            coordinatorLayout,
            child,
            target,
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            type
        )
    }
}
