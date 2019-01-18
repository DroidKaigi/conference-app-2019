package io.github.droidkaigi.confsched2019.floormap.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.widget.NestedScrollView

class DisablePinchNestedScrollView : NestedScrollView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        this.requestDisallowInterceptTouchEvent(event.pointerCount != 1)
        return super.dispatchTouchEvent(event)
    }
}
