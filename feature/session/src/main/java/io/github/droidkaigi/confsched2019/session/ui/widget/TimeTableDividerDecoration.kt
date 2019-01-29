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
import io.github.droidkaigi.confsched2019.session.ui.item.TabularSpacerItem
import io.github.droidkaigi.confsched2019.session.ui.item.TabularSpeechSessionItem
import java.util.concurrent.TimeUnit

class TimeTableDividerDecoration(
    private val lineColor: Int,
    private val lineWidth: Float,
    private val dashLineInterval: Float,
    private val pxPerMin: Int,
    private val groupAdapter: GroupAdapter<*>
) : RecyclerView.ItemDecoration() {

    constructor(context: Context, groupAdapter: GroupAdapter<*>) : this(
        ContextCompat.getColor(context, R.color.gray3),
        context.resources.getDimension(R.dimen.session_bottom_sheet_left_time_line_width),
        context.resources.getDimension(R.dimen.session_bottom_sheet_left_time_line_interval),
        context.resources.getDimensionPixelSize(R.dimen.tabular_form_px_per_minute),
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
        val referenceView = parent.getChildAt(0) ?: return
        val referenceStart =
            when (val item = groupAdapter.getItem(parent.getChildAdapterPosition(referenceView))) {
                is TabularSpeechSessionItem -> item.session.startTime.unixMillisLong
                is TabularServiceSessionItem -> item.session.startTime.unixMillisLong
                is TabularSpacerItem -> item.startUnixMillis
                else -> return
            }
        val childrenStart = parent.children.mapNotNull {
            when (val item = groupAdapter.getItem(parent.getChildAdapterPosition(it))) {
                is TabularSpeechSessionItem -> item.session.startTime.unixMillisLong
                is TabularServiceSessionItem -> item.session.startTime.unixMillisLong
                is TabularSpacerItem -> item.startUnixMillis
                else -> null
            }
        }
        val minStart = childrenStart.min() ?: 0
        val maxEnd = childrenStart.max() ?: 0
        (0 until groupAdapter.itemCount)
            .mapNotNull {
                val startTime = when (val item = groupAdapter.getItem(it)) {
                    is TabularSpeechSessionItem -> item.session.startTime
                    is TabularServiceSessionItem -> item.session.startTime
                    else -> return@mapNotNull null
                }
                if (startTime.unixMillisLong in minStart..maxEnd) startTime else null
            }
            .distinctBy { it.unixMillisLong }
            .forEach {
                val gapHeight =
                    TimeUnit.MILLISECONDS.toMinutes(it.unixMillisLong - referenceStart)
                        .toInt() * pxPerMin
                val top = referenceView.top + gapHeight
                c.drawLine(
                    left,
                    top.toFloat(),
                    parent.right.toFloat(),
                    top.toFloat(),
                    dashLinePaint
                )
            }
    }
}
