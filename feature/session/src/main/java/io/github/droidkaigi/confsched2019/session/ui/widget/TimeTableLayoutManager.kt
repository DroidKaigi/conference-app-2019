package io.github.droidkaigi.confsched2019.session.ui.widget

import android.util.SparseArray
import androidx.recyclerview.widget.RecyclerView

class TimeTableLayoutManager(private val periodLookUp: (position: Int) -> PeriodInfo) :
    RecyclerView.LayoutManager() {

    class PeriodInfo(val startEpochMilli: Long, val endEpochMilli: Long, val columnNumber: Int)

    private data class Period(
        val startEpochMilli: Long,
        val endEpochMilli: Long,
        val columnNumber: Int,
        val position: Int
    ) {
        val startEpochMin = (startEpochMilli / 1000 / 60).toInt()
        val endEpochMin = (endEpochMilli / 1000 / 60).toInt()
        val durationMin = endEpochMin - startEpochMin
    }

    private val parentLeft get() = paddingLeft
    private val parentTop get() = paddingTop
    private val parentRight get() = width - paddingRight
    private val parentBottom get() = height - paddingBottom

    private val columns = HashMap<Int, SparseArray<Period>>()
    private val periods = SparseArray<Period>()

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.WRAP_CONTENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )
    }

    private fun calculateLayout() {
        this.periods.clear()
        this.columns.clear()
        (0 until itemCount)
            .map {
                val periodInfo = periodLookUp(it)
                Period(
                    periodInfo.startEpochMilli,
                    periodInfo.endEpochMilli,
                    periodInfo.columnNumber,
                    it
                )
            }
            .forEachIndexed { i, period ->
                periods.put(i, period)
                val list = columns.getOrPut(period.columnNumber) { SparseArray() }
                list.put(list.size(), period)
            }
    }
}
