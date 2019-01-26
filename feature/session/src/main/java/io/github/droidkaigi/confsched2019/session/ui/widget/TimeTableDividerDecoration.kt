package io.github.droidkaigi.confsched2019.session.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.ui.item.TabularServiceSessionItem
import io.github.droidkaigi.confsched2019.session.ui.item.TabularSpeechSessionItem

class TimeTableDividerDecoration(
    private val columnCount: Int,
    private val lineColor: Int,
    private val lineWidth: Float,
    private val dashLineInterval: Float,
    private val groupAdapter: GroupAdapter<*>
) : RecyclerView.ItemDecoration() {

    constructor(context: Context, columnCount: Int, groupAdapter: GroupAdapter<*>) : this(
        columnCount,
        ContextCompat.getColor(context, R.color.gray3),
        context.resources.getDimension(R.dimen.session_bottom_sheet_left_time_line_width),
        context.resources.getDimension(R.dimen.session_bottom_sheet_left_time_line_interval),
        groupAdapter
    )

    private val straightLinePaint = Paint().apply {
        color = lineColor
        isAntiAlias = true
        strokeWidth = lineWidth
        style = Paint.Style.STROKE
    }

    private val dashLinePaint = Paint().apply {
        color = lineColor
        isAntiAlias = true
        pathEffect = DashPathEffect(floatArrayOf(dashLineInterval, dashLineInterval), 0f)
        strokeWidth = lineWidth
        style = Paint.Style.STROKE
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        // draw vertical line
        val left = parent.children.map { it.left }.min()?.toFloat() ?: return
        parent.children
            .distinctBy { it.right }
            .forEach {
                c.drawLine(
                    it.right.toFloat(),
                    parent.top.toFloat(),
                    it.right.toFloat(),
                    parent.bottom.toFloat(),
                    straightLinePaint
                )
            }
        // draw horizontal line
        parent.children
            .filter {
                val childAdapterPosition = parent.getChildAdapterPosition(it)
                val item = groupAdapter.getItem(childAdapterPosition)
                childAdapterPosition >= columnCount &&
                    (item is TabularServiceSessionItem || item is TabularSpeechSessionItem)
            }
            .forEach {
                c.drawLine(
                    left,
                    it.top.toFloat(),
                    parent.right.toFloat(),
                    it.top.toFloat(),
                    dashLinePaint
                )
            }
    }
}
