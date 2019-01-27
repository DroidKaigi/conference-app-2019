package io.github.droidkaigi.confsched2019.session.ui.widget

import android.graphics.Rect
import android.util.SparseArray
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.TimeUnit

// TODO: Implement View Recycling on scrolling
// TODO: save first visible item position or scroll position and restore it
// TODO: implement scrollToPosition
class TimeTableLayoutManager(
    private val columnWidth: Int,
    private val pxPerMinute: Int,
    private val periodLookUp: (position: Int) -> PeriodInfo
) : RecyclerView.LayoutManager() {

    class PeriodInfo(val startUnixMillis: Long, val endUnixMillis: Long, val columnNumber: Int)

    private data class Period(
        val startUnixMillis: Long,
        val endUnixMillis: Long,
        val columnNumber: Int,
        val position: Int
    ) {
        val startUnixMin = TimeUnit.MILLISECONDS.toMinutes(startUnixMillis).toInt()
        val endUnixMin = TimeUnit.MILLISECONDS.toMinutes(endUnixMillis).toInt()
        val durationMin = endUnixMin - startUnixMin
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

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (itemCount == 0) {
            detachAndScrapAttachedViews(recycler)
            periods.clear()
            columns.clear()
            return
        }

        detachAndScrapAttachedViews(recycler)
        calculateColumns()

        var xOffset = parentLeft
        for (columnNumber in 0 until columns.size) {
            xOffset += fillColumn(columnNumber, 0, xOffset, parentTop, recycler)
        }
    }

    private fun fillColumn(
        columnNumber: Int,
        fromPeriodPositionInColumn: Int,
        offsetX: Int,
        startY: Int,
        recycler: RecyclerView.Recycler
    ): Int {
        val periods = columns[columnNumber] ?: return 0
        var yOffset = startY
        var columnWidth = 0
        for (i in fromPeriodPositionInColumn until periods.size()) {
            val period = periods[i]
            val view = recycler.getViewForPosition(period.position)
            addView(view)
            measureChild(view, period)
            val width = getDecoratedMeasuredWidth(view)
            val height = getDecoratedMeasuredHeight(view)
            val left = offsetX
            val top = yOffset
            val right = left + width
            val bottom = top + height
            layoutDecorated(view, left, top, right, bottom)

            columnWidth = width
            yOffset = bottom
        }
        return columnWidth
    }

    private fun measureChild(view: View, period: Period) {
        val lp = view.layoutParams as RecyclerView.LayoutParams
        lp.width = columnWidth
        lp.height = period.durationMin * pxPerMinute

        val insets = Rect().apply { calculateItemDecorationsForChild(view, this) }
        val widthSpec = getChildMeasureSpec(
            width,
            widthMode,
            paddingLeft + paddingRight + insets.left + insets.right,
            lp.width,
            true
        )
        val heightSpec = getChildMeasureSpec(
            height,
            heightMode,
            paddingTop + paddingBottom + insets.top + insets.bottom,
            lp.height,
            true
        )
        view.measure(widthSpec, heightSpec)
    }

    private fun calculateColumns() {
        periods.clear()
        columns.clear()
        (0 until itemCount)
            .map {
                val periodInfo = periodLookUp(it)
                Period(
                    periodInfo.startUnixMillis,
                    periodInfo.endUnixMillis,
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
