package io.github.droidkaigi.confsched2019.session.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.soywiz.klock.DateFormat
import com.soywiz.klock.format
import com.xwray.groupie.GroupAdapter
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.ui.item.TabularServiceSessionItem
import io.github.droidkaigi.confsched2019.session.ui.item.TabularSpacerItem
import io.github.droidkaigi.confsched2019.session.ui.item.TabularSpeechSessionItem
import java.util.concurrent.TimeUnit

// FIXME: TIMEZONE
class TimeTableTimeLabelDecoration(
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

    // set padding to parent RecyclerView. room number 0 isn't always at the left
    // or specify the room at the left
//    override fun getItemOffsets(
//        outRect: Rect,
//        view: View,
//        parent: RecyclerView,
//        state: RecyclerView.State
//    ) {
//        super.getItemOffsets(outRect, view, parent, state)
//        val roomNumber =
//            when (val item = groupAdapter.getItem(parent.getChildAdapterPosition(view))) {
//                is TabularServiceSessionItem -> item.session.room
//                is TabularSpeechSessionItem -> item.session.room
//                is TabularSpacerItem -> item.room
//                else -> return
//            }.sequentialNumber
//
//        if (roomNumber == 0) outRect.left = labelWidth.toInt()
//    }

    // caches for horizontal dividers and time labels
    private var referenceView: View? = null
    private var referenceStart: Long? = null
    private var sessionStartUnixMilliList: List<Long>? = null

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
        referenceView = parent.getChildAt(0)
        val referenceView = referenceView ?: return
        referenceStart =
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
        val minStart = childrenStart.min() ?: return
        val maxEnd = childrenStart.max() ?: return
        sessionStartUnixMilliList = (0 until groupAdapter.itemCount)
            .mapNotNull {
                val startEpochMillis = when (val item = groupAdapter.getItem(it)) {
                    is TabularSpeechSessionItem -> item.session.startTime.unixMillisLong
                    is TabularServiceSessionItem -> item.session.startTime.unixMillisLong
                    else -> return@mapNotNull null
                }
                if (startEpochMillis in minStart..maxEnd) startEpochMillis else null
            }
            .distinct()

        val referenceStart = referenceStart ?: return
        sessionStartUnixMilliList?.forEach {
            val gapHeight =
                TimeUnit.MILLISECONDS.toMinutes(it - referenceStart)
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

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        // draw background
        c.drawRect(
            Rect(0, parent.top, labelWidth.toInt(), parent.bottom),
            backgroundPaint
        )

        // draw time
        val referenceView = referenceView ?: return
        val referenceStart = referenceStart ?: return
        sessionStartUnixMilliList?.forEach {
            val gapHeight =
                TimeUnit.MILLISECONDS.toMinutes(it - referenceStart)
                    .toInt() * pxPerMin
            val timeText = dateFormat.format(it)
            val top = referenceView.top + gapHeight
            val rect =
                Rect(0, top, labelWidth.toInt(), top + textHeight)
            val baseX = rect.centerX().toFloat() - textPaint.measureText(timeText) / 2f
            val baseY =
                rect.centerY() - (textPaint.fontMetrics.ascent +
                    textPaint.fontMetrics.descent) / 2f
            c.drawText(timeText, baseX, baseY, textPaint)
        }
    }
}
