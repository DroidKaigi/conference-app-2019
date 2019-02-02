package io.github.droidkaigi.confsched2019.session.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Rect
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.soywiz.klock.DateFormat
import com.soywiz.klock.DateTimeTz
import com.soywiz.klock.hours
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.ui.item.TabularServiceSessionItem
import io.github.droidkaigi.confsched2019.session.ui.item.TabularSpacerItem
import io.github.droidkaigi.confsched2019.session.ui.item.TabularSpeechSessionItem
import java.util.concurrent.TimeUnit

class CurrentTimeLineDecoration(
    private val labelWidth: Float,
    private val labelHeight: Float,
    private val labelTextSize: Float,
    private val labelBackgroundColor: Int,
    private val labelTextColor: Int,
    private val lineColor: Int,
    private val lineWidth: Float,
    private val dashLineInterval: Float,
    private val pxPerMin: Int,
    private val groupAdapter: GroupAdapter<*>
): RecyclerView.ItemDecoration() {

    constructor(context: Context, groupAdapter: GroupAdapter<*>) : this(
        context.resources.getDimension(R.dimen.tabular_form_time_label_width),
        context.resources.getDimension(R.dimen.tabular_form_room_label_height),
        context.resources.getDimension(R.dimen.tabular_form_time_label_text_size),
        ContextCompat.getColor(context, R.color.colorPrimary),
        Color.WHITE,
        ContextCompat.getColor(context, R.color.colorPrimary),
        context.resources.getDimension(R.dimen.session_bottom_sheet_left_time_line_width),
        context.resources.getDimension(R.dimen.session_bottom_sheet_left_time_line_interval),
        context.resources.getDimensionPixelSize(R.dimen.tabular_form_px_per_minute),
        groupAdapter
    )

    private val textPaint = Paint().apply {
        color = labelTextColor
        isAntiAlias = true
        textSize = labelTextSize
    }

    private val textBackground = Paint().apply {
        color = labelBackgroundColor
    }

    private val line = Paint().apply {
        color = lineColor
        isAntiAlias = true
        pathEffect = DashPathEffect(floatArrayOf(dashLineInterval, dashLineInterval), 0f)
        strokeWidth = lineWidth
        style = Paint.Style.STROKE
    }

    private val dateFormat: DateFormat = DateFormat("HH:mm")

    private val textHeightHalf =
        (Rect().apply { textPaint.getTextBounds("00:00", 0, "00:00".length - 1, this) }.height()) / 2

    private val textWidth =
        Rect().apply { textPaint.getTextBounds("00:00", 0, "00:00".length, this) }.width()

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        val currentTime = System.currentTimeMillis()
        val timeText = dateFormat
            .format(DateTimeTz.fromUnixLocal(currentTime).addOffset(9.hours))

        val originView = parent.getChildAt(0)
        val originStartUnixMillis = groupAdapter.getItem(parent.getChildAdapterPosition(originView))
            .startUnixMillis

        val gapHeight = TimeUnit.MILLISECONDS
            .toMinutes(currentTime - originStartUnixMillis)
            .toFloat() * pxPerMin

        val height = originView.top + gapHeight
        if (height < labelHeight) return

        c.drawLine(labelWidth, height, parent.right.toFloat(), height, line)
        c.drawRect(labelWidth, height - textHeightHalf, labelWidth + textWidth.toFloat(), height + textHeightHalf, textBackground)
        c.drawText(timeText, labelWidth, height + textHeightHalf, textPaint)
    }

    private inline val Item<*>.startUnixMillis: Long
        get() {
            return when (this) {
                is TabularSpeechSessionItem -> session.startTime.unixMillisLong
                is TabularServiceSessionItem -> session.startTime.unixMillisLong
                is TabularSpacerItem -> startUnixMillis
                else -> 0
            }
        }
}
