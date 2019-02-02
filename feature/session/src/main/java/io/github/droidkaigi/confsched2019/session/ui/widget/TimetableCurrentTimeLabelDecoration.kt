package io.github.droidkaigi.confsched2019.session.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.CornerPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.soywiz.klock.DateFormat
import com.soywiz.klock.DateTimeTz
import com.soywiz.klock.hours
import com.xwray.groupie.GroupAdapter
import io.github.droidkaigi.confsched2019.session.R

class TimetableCurrentTimeLabelDecoration(
    private val labelWidth: Float,
    private val RoomLabelHeight: Float,
    private val labelTextSize: Float,
    private val labelTextColor: Int,
    private val lineColor: Int,
    private val lineWidth: Float,
    private val pxPerMin: Int,
    private val labelPadding: Float,
    private val groupAdapter: GroupAdapter<*>
) : TimetableCurrentTimeLineDecoration(
    labelWidth,
    RoomLabelHeight,
    lineColor,
    lineWidth,
    pxPerMin,
    groupAdapter
) {

    constructor(context: Context, groupAdapter: GroupAdapter<*>) : this(
        context.resources.getDimension(R.dimen.tabular_form_time_label_width),
        context.resources.getDimension(R.dimen.tabular_form_room_label_height),
        context.resources.getDimension(R.dimen.tabular_form_time_label_text_size),
        Color.WHITE,
        ContextCompat.getColor(context, R.color.red1),
        context.resources.getDimension(R.dimen.tabular_form_line_width_bold),
        context.resources.getDimensionPixelSize(R.dimen.tabular_form_px_per_minute),
        context.resources.getDimension(R.dimen.tabular_form_current_time_label_padding),
        groupAdapter
    )

    private val textPaint = Paint().apply {
        color = labelTextColor
        isAntiAlias = true
        textSize = labelTextSize
    }

    private val line = Paint().apply {
        color = lineColor
        isAntiAlias = true
        strokeWidth = lineWidth
        style = Paint.Style.STROKE
    }

    private val dateFormat: DateFormat = DateFormat("HH:mm")

    private val textHeightHalf =
        Rect().apply {
            textPaint.getTextBounds("00:00", 0, "00:00".length - 1, this)
        }.height() / 2

    private val textWidth =
        Rect().apply { textPaint.getTextBounds("00:00", 0, "00:00".length, this) }.width()

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {

        val currentTime = System.currentTimeMillis()
        val timeText = dateFormat
            .format(DateTimeTz.fromUnixLocal(currentTime).addOffset(9.hours))

        val height = calcLineHeight(parent, currentTime)
        if (height < RoomLabelHeight) return

        c.drawLine(labelWidth + labelPadding, height, parent.right.toFloat(), height, line)
        drawBackgroundShape(c, height)
        c.drawText(timeText, labelWidth + labelPadding * 2, height + textHeightHalf, textPaint)
    }

    private fun drawBackgroundShape(c: Canvas, height: Float) {
        // TODO: apply correct design. Please check out.

        val paint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL_AND_STROKE
            color = lineColor

            val cornerPathEffect = CornerPathEffect(6f)
            pathEffect = cornerPathEffect
        }
        val path = Path().apply {
            moveTo(labelWidth, height)
            lineTo(labelWidth + labelPadding,
                height - textHeightHalf - labelPadding)
            lineTo(labelWidth + textWidth + labelPadding * 4,
                height - textHeightHalf - labelPadding)
            lineTo(labelWidth + textWidth + labelPadding * 4,
                height + textHeightHalf + labelPadding)
            lineTo(labelWidth + labelPadding,
                height + textHeightHalf + labelPadding)
            lineTo(labelWidth, height)
        }
        c.drawPath(path, paint)
    }
}
