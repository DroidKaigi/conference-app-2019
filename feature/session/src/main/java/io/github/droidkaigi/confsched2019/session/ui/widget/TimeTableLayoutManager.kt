package io.github.droidkaigi.confsched2019.session.ui.widget

import android.graphics.Rect
import android.util.SparseArray
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.min

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
        val startUnixMin: Int,
        val endUnixMin: Int,
        val columnNumber: Int,
        val adapterPosition: Int,
        val positionInColumn: Int
    ) {
        val durationMin = endUnixMin - startUnixMin
    }

    private val parentLeft get() = paddingLeft
    private val parentTop get() = paddingTop
    private val parentRight get() = width - paddingRight
    private val parentBottom get() = height - paddingBottom

    private val periods = ArrayList<Period>()
    private val columns = SparseArray<ArrayList<Period>>()

    private var firstStartUnixMin = NO_TIME
    private var lastEndUnixMin = NO_TIME

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
        for (columnNumber in 0 until columns.size()) {
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
        for (i in fromPeriodPositionInColumn until periods.size) {
            val period = periods[i]
            val view = recycler.getViewForPosition(period.adapterPosition)
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
        firstStartUnixMin = NO_TIME
        lastEndUnixMin = NO_TIME

        (0 until itemCount).forEach {
            val periodInfo = periodLookUp(it)
            val column = columns.getOrPut(periodInfo.columnNumber) { ArrayList() }

            val period = Period(
                TimeUnit.MILLISECONDS.toMinutes(periodInfo.startUnixMillis).toInt(),
                TimeUnit.MILLISECONDS.toMinutes(periodInfo.endUnixMillis).toInt(),
                periodInfo.columnNumber,
                adapterPosition = it,
                positionInColumn = column.size
            )
            periods.add(period)
            column.add(period)

            if (it == 0) {
                firstStartUnixMin = period.startUnixMin
                lastEndUnixMin = period.endUnixMin
            } else {
                firstStartUnixMin = min(period.startUnixMin, firstStartUnixMin)
                lastEndUnixMin = max(period.endUnixMin, lastEndUnixMin)
            }
        }
    }

    private inline fun <E> SparseArray<E>.getOrPut(key: Int, defaultValue: () -> E): E {
        val value = get(key)
        return if (value == null) {
            val answer = defaultValue()
            put(key, answer)
            answer
        } else {
            value
        }
    }

    companion object {
        private const val NO_TIME = -1
    }
}
