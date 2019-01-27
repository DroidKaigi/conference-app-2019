package io.github.droidkaigi.confsched2019.session.ui.widget

import android.graphics.Rect
import android.util.SparseArray
import android.util.SparseIntArray
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import androidx.recyclerview.widget.RecyclerView.Recycler
import androidx.recyclerview.widget.RecyclerView.State
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

    private class Anchor {
        val top = SparseIntArray()
        val bottom = SparseIntArray()
        var leftColumn = NO_POSITION
        var rightColumn = NO_POSITION

        fun reset() {
            top.clear()
            bottom.clear()
            leftColumn = NO_POSITION
            rightColumn = NO_POSITION
        }
    }

    enum class Direction {
        LEFT, TOP, RIGHT, BOTTOM
    }

    private val parentLeft get() = paddingLeft
    private val parentTop get() = paddingTop
    private val parentRight get() = width - paddingRight
    private val parentBottom get() = height - paddingBottom

    private val periods = ArrayList<Period>()
    private val columns = SparseArray<ArrayList<Period>>()
    private val anchor = Anchor()

    private var firstStartUnixMin = NO_TIME
    private var lastEndUnixMin = NO_TIME

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.WRAP_CONTENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onLayoutChildren(recycler: Recycler, state: State) {
        if (itemCount == 0) {
            detachAndScrapAttachedViews(recycler)
            periods.clear()
            columns.clear()
            anchor.reset()
            return
        }

        detachAndScrapAttachedViews(recycler)
        anchor.reset()
        calculateColumns()

        var offsetX = parentLeft
        anchor.leftColumn = 0
        for (columnNum in 0 until columns.size()) {
            offsetX += fillColumnHorizontally(columnNum, 0, offsetX, parentTop, true, recycler)
            if (offsetX > parentRight) {
                anchor.rightColumn = columnNum
                break
            }
        }
    }

    private fun addPeriod(
        period: Period,
        direction: Direction,
        offsetX: Int,
        offsetY: Int,
        recycler: Recycler
    ): Pair<Int, Int> {
        val view = recycler.getViewForPosition(period.adapterPosition)
        addView(view)
        measureChild(view, period)
        val width = getDecoratedMeasuredWidth(view)
        val height = getDecoratedMeasuredHeight(view)
        val left = if (direction == Direction.LEFT) offsetX - width else offsetX
        val top = if (direction == Direction.TOP) offsetY - height else offsetY
        val right = left + width
        val bottom = top + height
        layoutDecorated(view, left, top, right, bottom)
        return width to height
    }

    private fun fillColumnHorizontally(
        columnNum: Int,
        startPositionInColumn: Int,
        offsetX: Int,
        startY: Int,
        isAppend: Boolean,
        recycler: Recycler
    ): Int {
        val periods = columns[columnNum] ?: return 0
        val direction = if (isAppend) Direction.RIGHT else Direction.LEFT
        var offsetY = startY
        var columnWidth = 0
        for (i in startPositionInColumn until periods.size) {
            val period = periods[i]
            val (width, height) = addPeriod(period, direction, offsetX, offsetY, recycler)

            offsetY += height

            if (i == startPositionInColumn) {
                anchor.top.put(columnNum, period.adapterPosition)
            }
            if (offsetY > parentBottom) {
                columnWidth = width
                anchor.bottom.put(columnNum, period.adapterPosition)
                break
            }
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
