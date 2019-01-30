package io.github.droidkaigi.confsched2019.session.ui.widget

import android.graphics.Rect
import android.os.Parcel
import android.os.Parcelable
import android.util.SparseArray
import android.util.SparseIntArray
import android.view.View
import androidx.core.util.forEach
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import androidx.recyclerview.widget.RecyclerView.Recycler
import androidx.recyclerview.widget.RecyclerView.State
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.min

// TODO: implement scrollToPosition
class TimeTableLayoutManager(
    private val columnWidth: Int,
    private val pxPerMinute: Int,
    private val shouldLoopHorizontally: Boolean,
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

    private data class SaveState(val position: Int, val left: Int, val top: Int) : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt()
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeInt(position)
            parcel.writeInt(left)
            parcel.writeInt(top)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<SaveState> {
            override fun createFromParcel(parcel: Parcel): SaveState {
                return SaveState(parcel)
            }

            override fun newArray(size: Int): Array<SaveState?> {
                return arrayOfNulls(size)
            }
        }
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

    private var saveState: SaveState? = null

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.WRAP_CONTENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        saveState = (state as? SaveState)
    }

    override fun onSaveInstanceState(): Parcelable? {
        if (childCount == 0) return null

        val view = findFirstVisibleView() ?: return null
        return SaveState(
            view.adapterPosition,
            getDecoratedLeft(view),
            getDecoratedTop(view)
        )
    }

    override fun onLayoutChildren(recycler: Recycler, state: State) {
        if (itemCount == 0) {
            detachAndScrapAttachedViews(recycler)
            periods.clear()
            columns.clear()
            anchor.reset()
            saveState = null
            return
        }

        anchor.reset()
        calculateColumns()

        val firstVisibleView = if (childCount > 0) findFirstVisibleView() else null
        val offsetX = saveState?.left ?: firstVisibleView?.let(this::getDecoratedLeft)
        val offsetY = saveState?.top ?: firstVisibleView?.let(this::getDecoratedTop)
        val period = saveState?.position?.let(periods::getOrNull)
            ?: firstVisibleView?.adapterPosition?.let(periods::getOrNull)
        detachAndScrapAttachedViews(recycler)
        if (offsetX != null && offsetY != null && period != null)
            relayoutChildren(offsetX, offsetY, period, recycler)
        else
            layoutChildren(recycler)
    }

    override fun onLayoutCompleted(state: State?) {
        saveState = null
    }

    override fun canScrollVertically() = true

    override fun canScrollHorizontally() = true

    override fun scrollVerticallyBy(dy: Int, recycler: Recycler, state: State): Int {
        if (dy == 0) return 0

        val scrollAmount = calculateVerticallyScrollAmount(dy)
        if (scrollAmount > 0) {
            // recycle
            anchor.top.forEach { columnNum, position ->
                val view = findViewByPosition(position) ?: return@forEach
                val bottom = getDecoratedBottom(view)
                if (bottom - scrollAmount < parentTop) {
                    val period = periods.getOrNull(position) ?: return@forEach
                    val nextPeriod = columns.get(columnNum).getOrNull(period.positionInColumn + 1)
                        ?: return@forEach
                    removeAndRecycleView(view, recycler)
                    anchor.top.put(columnNum, nextPeriod.adapterPosition)
                }
            }
            // append
            anchor.bottom.forEach { columnNum, position ->
                val view = findViewByPosition(position) ?: return@forEach
                val bottom = getDecoratedBottom(view)
                if (bottom - scrollAmount < parentBottom) {
                    val left = getDecoratedLeft(view)
                    val period = periods.getOrNull(position) ?: return@forEach
                    val nextPeriod = columns.get(columnNum).getOrNull(period.positionInColumn + 1)
                        ?: return@forEach
                    addPeriod(nextPeriod, Direction.BOTTOM, left, bottom, recycler)
                    anchor.bottom.put(columnNum, nextPeriod.adapterPosition)
                }
            }
        } else {
            // recycle
            anchor.bottom.forEach { columnNum, position ->
                val view = findViewByPosition(position) ?: return@forEach
                val top = getDecoratedTop(view)
                if (top - scrollAmount > parentBottom) {
                    val period = periods.getOrNull(position) ?: return@forEach
                    val nextPeriod = columns.get(columnNum).getOrNull(period.positionInColumn - 1)
                        ?: return@forEach
                    removeAndRecycleView(view, recycler)
                    anchor.bottom.put(columnNum, nextPeriod.adapterPosition)
                }
            }
            // prepend
            anchor.top.forEach { columnNum, position ->
                val view = findViewByPosition(position) ?: return@forEach
                val top = getDecoratedTop(view)
                if (top - scrollAmount > parentTop) {
                    val left = getDecoratedLeft(view)
                    val period = periods.getOrNull(position) ?: return@forEach
                    val nextPeriod = columns.get(columnNum).getOrNull(period.positionInColumn - 1)
                        ?: return@forEach
                    addPeriod(nextPeriod, Direction.TOP, left, top, recycler)
                    anchor.top.put(columnNum, nextPeriod.adapterPosition)
                }
            }
        }

        offsetChildrenVertical(-scrollAmount)
        return scrollAmount
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: Recycler, state: State): Int {
        if (dx == 0) return 0

        val rightView = findRightView() ?: return 0
        val leftView = findLeftView() ?: return 0
        val right = getDecoratedRight(rightView)
        val left = getDecoratedLeft(leftView)
        val scrollAmount = calculateHorizontallyScrollAmount(dx, left, right)
        if (scrollAmount > 0) {
            // recycle
            if (getDecoratedRight(leftView) - dx < parentLeft) {
                findViewsByColumn(anchor.leftColumn).forEach { removeAndRecycleView(it, recycler) }
                anchor.leftColumn.getNextColumn()?.let { anchor.leftColumn = it }
            }
            // append
            if (right - scrollAmount < parentRight) {
                val nextColumnNum = anchor.rightColumn.getNextColumn() ?: return 0

                val topView = findTopView() ?: return 0
                val topPeriod = periods[topView.adapterPosition]
                val startPeriodInColumn =
                    calculateStartPeriodInColumn(nextColumnNum, getDecoratedTop(topView), topPeriod)
                        ?: return 0
                val offsetY = getDecoratedTop(topView) +
                    (startPeriodInColumn.startUnixMin - topPeriod.startUnixMin) * pxPerMinute
                fillColumnHorizontally(
                    nextColumnNum,
                    startPeriodInColumn.positionInColumn,
                    right,
                    offsetY,
                    true,
                    recycler
                )
                anchor.rightColumn = nextColumnNum
            }
        } else {
            // recycle
            if (getDecoratedLeft(rightView) - dx > parentRight) {
                findViewsByColumn(anchor.rightColumn).forEach { removeAndRecycleView(it, recycler) }
                anchor.rightColumn.getPreviousColumn()?.let { anchor.rightColumn = it }
            }
            // prepend
            if (left - scrollAmount > parentLeft) {
                val previousColumn = anchor.leftColumn.getPreviousColumn() ?: return 0

                val topView = findTopView() ?: return 0
                val topPeriod = periods[topView.adapterPosition]
                val startPeriodInColumn =
                    calculateStartPeriodInColumn(
                        previousColumn,
                        getDecoratedTop(topView),
                        topPeriod
                    ) ?: return 0
                val offsetY = getDecoratedTop(topView) +
                    (startPeriodInColumn.startUnixMin - topPeriod.startUnixMin) * pxPerMinute
                fillColumnHorizontally(
                    previousColumn,
                    startPeriodInColumn.positionInColumn,
                    left,
                    offsetY,
                    false,
                    recycler
                )
                anchor.leftColumn = previousColumn
            }
        }

        offsetChildrenHorizontal(-scrollAmount)
        return dx
    }

    private fun layoutChildren(recycler: Recycler) {
        val offsetY = parentTop
        var offsetX = parentLeft
        for (i in 0 until columns.size()) {
            val columnNumber = columns.keyAt(i)
            if (i == 0) anchor.leftColumn = columnNumber
            offsetX += fillColumnHorizontally(columnNumber, 0, offsetX, offsetY, true, recycler)
            if (offsetX > parentRight) {
                anchor.rightColumn = columnNumber
                break
            }
        }
    }

    private fun relayoutChildren(startX: Int, startY: Int, topPeriod: Period, recycler: Recycler) {
        var offsetX = startX
        anchor.leftColumn = topPeriod.columnNumber
        val columnCount = columns.size()
        var i = columns.indexOfKey(topPeriod.columnNumber)
        while (i < columnCount) {
            val columnNumber = columns.keyAt(i)
            val startPositionInColumn =
                calculateStartPeriodInColumn(columnNumber, startY, topPeriod) ?: continue
            val offsetY = startY +
                (startPositionInColumn.startUnixMin - topPeriod.startUnixMin) * pxPerMinute
            offsetX += fillColumnHorizontally(
                columnNumber,
                startPositionInColumn.positionInColumn,
                offsetX,
                offsetY,
                true,
                recycler
            )

            if (i == columnCount - 1) i = 0 else i++
            if (offsetX > parentRight) {
                anchor.rightColumn = columnNumber
                break
            }
        }
    }

    private fun calculateVerticallyScrollAmount(dy: Int): Int {
        return if (dy > 0) {
            val bottomView = findBottomView() ?: return 0
            val period = periods.getOrNull(bottomView.adapterPosition) ?: return 0
            val bottom = getDecoratedBottom(bottomView)
            if (period.endUnixMin == lastEndUnixMin) if (bottom == parentBottom) 0 else min(
                dy,
                bottom - parentBottom
            )
            else dy
        } else {
            val topView = findTopView() ?: return 0
            val period = periods.getOrNull(topView.adapterPosition) ?: return 0
            val top = getDecoratedTop(topView)
            if (period.startUnixMin == firstStartUnixMin) if (top == parentTop) 0 else max(
                dy,
                top - parentTop
            )
            else dy
        }
    }

    private fun calculateHorizontallyScrollAmount(dx: Int, left: Int, right: Int): Int {
        if (shouldLoopHorizontally) return dx

        return if (dx > 0) {
            if (anchor.rightColumn.isLastColumn()) if (right <= parentRight) 0
            else min(dx, right - parentRight)
            else dx
        } else {
            if (anchor.leftColumn.isFirstColumn()) if (left >= parentLeft) 0
            else max(dx, left - parentLeft)
            else dx
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

    private fun findTopView(): View? {
        var minTop: Int? = null
        var minView: View? = null
        anchor.top.forEach { _, position ->
            val view = findViewByPosition(position) ?: return@forEach
            val top = getDecoratedTop(view)
            if (minView == null) {
                minView = view
                minTop = top
                return@forEach
            }
            minTop?.let {
                if (top < it) {
                    minView = view
                    minTop = top
                }
            }
        }
        return minView
    }

    private fun findBottomView(): View? {
        var maxBottom: Int? = null
        var maxView: View? = null
        anchor.bottom.forEach { _, position ->
            val view = findViewByPosition(position) ?: return@forEach
            val bottom = getDecoratedBottom(view)
            if (maxView == null) {
                maxView = view
                maxBottom = bottom
                return@forEach
            }
            maxBottom?.let {
                if (bottom > it) {
                    maxView = view
                    maxBottom = bottom
                }
            }
        }
        return maxView
    }

    private fun findLeftView() = findViewByColumn(anchor.leftColumn)

    private fun findRightView() = findViewByColumn(anchor.rightColumn)

    private fun findViewByColumn(columnNumber: Int): View? {
        (0 until childCount).forEach { layoutPosition ->
            val view = getChildAt(layoutPosition) ?: return@forEach
            val period = periods[view.adapterPosition]
            if (period.columnNumber == columnNumber) return view
        }
        return null
    }

    private fun findViewsByColumn(columnNumber: Int): List<View> {
        return (0 until childCount).mapNotNull { layoutPosition ->
            val view = getChildAt(layoutPosition) ?: return@mapNotNull null
            val period = periods[view.adapterPosition]
            if (period.columnNumber == columnNumber) view else null
        }
    }

    private fun findFirstVisibleView(): View? {
        return (0 until childCount).asSequence()
            .mapNotNull(this::getChildAt)
            .filter { getDecoratedLeft(it) <= parentLeft }
            .minWith(Comparator { viewL, viewR ->
                getDecoratedTop(viewL) - getDecoratedTop(viewR)
            })
    }

    private fun calculateStartPeriodInColumn(
        columnNumber: Int,
        top: Int,
        topPeriod: Period
    ): Period? {
        val periods = columns[columnNumber] ?: return null
        var maxTopPeriod: Period? = null
        periods.filter {
            it.startUnixMin <= topPeriod.endUnixMin && it.endUnixMin >= topPeriod.startUnixMin
        }
            .forEach { period ->
                val gapHeight = (period.startUnixMin - topPeriod.startUnixMin) * pxPerMinute
                if (top + gapHeight <= parentTop)
                    maxTopPeriod = maxTopPeriod?.let {
                        if (it.startUnixMin < period.startUnixMin) period else it
                    } ?: period
            }
        return maxTopPeriod
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

    private fun Int.isFirstColumn() = columns.indexOfKey(this) == 0

    private fun Int.isLastColumn() = columns.indexOfKey(this) == columns.size() - 1

    private fun Int.getNextColumn(): Int? {
        val index = columns.indexOfKey(this)
        return if (index == columns.size() - 1)
            if (shouldLoopHorizontally) columns.keyAt(0) else null
        else
            columns.keyAt(index + 1)
    }

    private fun Int.getPreviousColumn(): Int? {
        val index = columns.indexOfKey(this)
        return if (index == 0)
            if (shouldLoopHorizontally) columns.keyAt(columns.size() - 1) else null
        else
            columns.keyAt(index - 1)
    }

    private inline val View.adapterPosition
        get() = (layoutParams as RecyclerView.LayoutParams).viewAdapterPosition

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
