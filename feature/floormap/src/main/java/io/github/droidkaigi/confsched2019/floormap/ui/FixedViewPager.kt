package io.github.droidkaigi.confsched2019.floormap.ui

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

/**
 * work around for PhotoView in ViewPager
 * [https://github.com/chrisbanes/PhotoView#issues-with-viewgroups]
 */
class FixedViewPager : ViewPager {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        try {
            return super.onInterceptTouchEvent(event)
        } catch (exception: IllegalArgumentException) {
            return false
        }
    }
}
