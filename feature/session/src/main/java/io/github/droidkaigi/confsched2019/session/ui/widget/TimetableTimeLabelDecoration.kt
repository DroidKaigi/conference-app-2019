package io.github.droidkaigi.confsched2019.session.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Rect
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.soywiz.klock.DateFormat
import com.soywiz.klock.DateTimeTz
import com.soywiz.klock.hours
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import io.github.droidkaigi.confsched2019.model.Session
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.ui.item.TabularServiceSessionItem
import io.github.droidkaigi.confsched2019.session.ui.item.TabularSpacerItem
import io.github.droidkaigi.confsched2019.session.ui.item.TabularSpeechSessionItem
import java.util.concurrent.TimeUnit

class TimetableTimeLabelDecoration(
    private val labelWidth: Float,
    private val labelTextSize: Float,
    private val labelBackgroundColor: Int,
    private val labelTextColor: Int,
    private val lineColor: Int,
    private val lineWidth: Float,
    private val dashLineInterval: Float,
    private val pxPerMin: Int,
    private val groupAdapter: GroupAdapter<*>
) : RecyclerView.ItemDecoration() {

    constructor(context: Context, groupAdapter: GroupAdapter<*>) : this(
        context.resources.getDimension(R.dimen.tabular_form_time_label_width),
        context.resources.getDimension(R.dimen.tabular_form_time_label_text_size),
        Color.WHITE,
        Color.BLACK,
        ContextCompat.getColor(context, R.color.gray3),
        context.resources.getDimension(R.dimen.session_bottom_sheet_left_time_line_width),
        context.resources.getDimension(R.dimen.session_bottom_sheet_left_time_line_interval),
        context.resources.getDimensionPixelSize(R.dimen.tabular_form_px_per_minute),
        groupAdapter
    )

    private val backgroundPaint = Paint().apply { color = labelBackgroundColor }

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

    private val textPaint = Paint().apply {
        color = labelTextColor
        isAntiAlias = true
        textSize = labelTextSize
    }

    private val dateFormat: DateFormat = DateFormat("HH:mm")

    private val textHeight =
        Rect().apply { textPaint.getTextBounds("00:00", 0, "00:00".length - 1, this) }.height()

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)

        val left = parent.children.map { it.left }.min()?.toFloat() ?: return
        drawVerticalLines(parent, c)

        val originView = parent.getChildAt(0)
        val originStartUnixMillis = groupAdapter.getItem(parent.getChildAdapterPosition(originView))
            .startUnixMillis ?: return

        findVisibleSessions(parent).forEach {
            val gapHeight = TimeUnit.MILLISECONDS
                .toMinutes(it.startTime.unixMillisLong - originStartUnixMillis)
                .toInt() * pxPerMin
            drawHorizontalLines(c, parent, left, originView.top + gapHeight)
        }

        drawTimeLabelBackground(c, parent)

        findVisibleSessions(parent).forEach {
            val gapHeight = TimeUnit.MILLISECONDS
                .toMinutes(it.startTime.unixMillisLong - originStartUnixMillis)
                .toInt() * pxPerMin
            drawTimeLabel(c, originView.top + gapHeight, it)
        }
    }

    private fun drawTimeLabelBackground(c: Canvas, parent: RecyclerView) {
        c.drawRect(
            Rect(0, parent.top, labelWidth.toInt(), parent.bottom),
            backgroundPaint
        )
    }

    private fun drawVerticalLines(parent: RecyclerView, c: Canvas) {
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
    }

    private fun drawHorizontalLines(c: Canvas, parent: RecyclerView, left: Float, top: Int) {
        c.drawLine(left, top.toFloat(), parent.right.toFloat(), top.toFloat(), dashLinePaint)
    }

    private fun drawTimeLabel(c: Canvas, top: Int, it: Session) {
        val timeText = dateFormat
            .format(DateTimeTz.fromUnixLocal(it.startTime.unixMillisLong).addOffset(9.hours))
        val rect =
            Rect(0, top, labelWidth.toInt(), top + textHeight)
        val baseX = rect.centerX().toFloat() - textPaint.measureText(timeText) / 2f
        val baseY =
            rect.centerY() - (textPaint.fontMetrics.ascent +
                textPaint.fontMetrics.descent) / 2f
        c.drawText(timeText, baseX, baseY, textPaint)
    }

    private fun findVisibleSessions(parent: RecyclerView): List<Session> {
        val minStart = parent.children.minBy { it.top }
            ?.let {
                val childAdapterPosition = parent.getChildAdapterPosition(it)
                if (childAdapterPosition == RecyclerView.NO_POSITION) {
                    return@let null
                }
                groupAdapter.getItem(childAdapterPosition).startUnixMillis
            }
            ?: return emptyList()
        val maxEnd = parent.children.maxBy { it.bottom }
            ?.let {
                val childAdapterPosition = parent.getChildAdapterPosition(it)
                if (childAdapterPosition == RecyclerView.NO_POSITION) {
                    return@let null
                }
                groupAdapter.getItem(childAdapterPosition).endUnixMillis
            }
            ?: return emptyList()

        return (0 until groupAdapter.itemCount)
            .mapNotNull {
                val session: Session = when (val item = groupAdapter.getItem(it)) {
                    is TabularSpeechSessionItem -> item.session
                    is TabularServiceSessionItem -> item.session
                    else -> return@mapNotNull null
                }
                if (session.startTime.unixMillisLong in minStart..maxEnd) session else null
            }
            .distinct()
    }

    private inline val Item<*>.startUnixMillis: Long?
        get() {
            return when (this) {
                is TabularSpeechSessionItem -> session.startTime.unixMillisLong
                is TabularServiceSessionItem -> session.startTime.unixMillisLong
                is TabularSpacerItem -> startUnixMillis
                else -> null
            }
        }

    private inline val Item<*>.endUnixMillis: Long?
        get() {
            return when (this) {
                is TabularSpeechSessionItem -> session.endTime.unixMillisLong
                is TabularServiceSessionItem -> session.endTime.unixMillisLong
                is TabularSpacerItem -> endUnixMillis
                else -> null
            }
        }
}
