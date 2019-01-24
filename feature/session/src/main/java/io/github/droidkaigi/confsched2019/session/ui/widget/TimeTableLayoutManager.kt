package io.github.droidkaigi.confsched2019.session.ui.widget

import androidx.recyclerview.widget.RecyclerView

class TimeTableLayoutManager: RecyclerView.LayoutManager() {
    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.WRAP_CONTENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )
    }
}
