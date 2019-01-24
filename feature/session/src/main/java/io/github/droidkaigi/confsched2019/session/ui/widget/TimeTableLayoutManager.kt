package io.github.droidkaigi.confsched2019.session.ui.widget

import android.util.SparseArray
import androidx.recyclerview.widget.RecyclerView

class TimeTableLayoutManager: RecyclerView.LayoutManager() {

    private val parentLeft get() = paddingLeft
    private val parentTop get() = paddingTop
    private val parentRight get() = width - paddingRight
    private val parentBottom get() = height - paddingBottom

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.WRAP_CONTENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )
    }
}
