package io.github.droidkaigi.confsched2019.session.ui.widget

import android.widget.LinearLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.BindingAdapter

@BindingAdapter("setToolbarTitle")
fun LinearLayout.setToolbarTitle(title: String) {
    (this.layoutParams as CoordinatorLayout.LayoutParams).behavior =
        SessionToolbarBehavior(context, null, title)
}
