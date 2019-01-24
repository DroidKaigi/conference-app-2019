package io.github.droidkaigi.confsched2019.session.ui.widget

import android.graphics.Rect
import android.util.SparseArray
import android.view.View
import androidx.recyclerview.widget.RecyclerView

// TODO: Implement View Recycling on scrolling
// TODO: save first visible item position or scroll position and restore it
// TODO: implement scrollToPosition
class TimeTableLayoutManager(
    private val columnWidth: Int,
    private val pxPerMinute: Int,
    private val periodLookUp: (position: Int) -> PeriodInfo
) : RecyclerView.LayoutManager() {

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

    override fun canScrollVertically() = true

    override fun canScrollHorizontally() = true

    override fun scrollVerticallyBy(
        dy: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State
    ): Int {
        if (dy == 0) return 0

        val scrollAmount = if (dy > 0) {
            val bottom = getChildAt(childCount - 1)?.let(this::getDecoratedBottom) ?: return 0
            if (bottom - dy < parentBottom) bottom - parentBottom else dy
        } else {
            val top = getChildAt(0)?.let(this::getDecoratedTop) ?: return 0
            if (top - dy > parentTop) top - parentTop else dy
        }

        offsetChildrenVertical(-scrollAmount)
        return scrollAmount
    }

    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Int {
        if (dx == 0) return 0

        val scrollAmount = if (dx > 0) {
            val right = getChildAt(childCount - 1)?.let(this::getDecoratedRight) ?: return 0
            if (right - dx < parentRight) right - parentRight else dx
        } else {
            val left = getChildAt(0)?.let(this::getDecoratedLeft) ?: return 0
            if (left - dx > parentLeft) left - parentLeft else dx
        }

        offsetChildrenHorizontal(-scrollAmount)
        return scrollAmount
    }

    override fun computeVerticalScrollRange(state: RecyclerView.State): Int {
        val top = getChildAt(0)?.let(this::getDecoratedTop) ?: return 0
        val bottom = getChildAt(childCount - 1)?.let(this::getDecoratedBottom) ?: return 0
        return bottom - top
    }

    override fun computeVerticalScrollExtent(state: RecyclerView.State): Int {
        return height - paddingTop - paddingBottom
    }

    override fun computeVerticalScrollOffset(state: RecyclerView.State): Int {
        return -(getChildAt(0)?.let(this::getDecoratedTop) ?: 0)
    }

    override fun computeHorizontalScrollRange(state: RecyclerView.State): Int {
        val left = getChildAt(0)?.let(this::getDecoratedLeft) ?: return 0
        val right = getChildAt(childCount - 1)?.let(this::getDecoratedRight) ?: return 0
        return right - left
    }

    override fun computeHorizontalScrollExtent(state: RecyclerView.State): Int {
        return width - paddingBottom - paddingRight
    }

    override fun computeHorizontalScrollOffset(state: RecyclerView.State): Int {
        return -(getChildAt(0)?.let(this::getDecoratedLeft) ?: 0)
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
