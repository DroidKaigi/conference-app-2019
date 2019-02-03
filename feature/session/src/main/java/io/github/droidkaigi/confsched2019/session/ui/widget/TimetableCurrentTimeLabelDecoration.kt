package io.github.droidkaigi.confsched2019.session.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.CornerPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import androidx.recyclerview.widget.RecyclerView
import com.soywiz.klock.DateFormat
import com.soywiz.klock.DateTimeTz
import com.soywiz.klock.hours
import com.xwray.groupie.GroupAdapter
import io.github.droidkaigi.confsched2019.session.R

class TimetableCurrentTimeLabelDecoration(
    context: Context,
    groupAdapter: GroupAdapter<*>
) : TimetableCurrentTimeLineDecoration(
    context,
    groupAdapter
) {
    private val labelPadding =
        context.resources.getDimension(R.dimen.tabular_form_current_time_label_padding)
    private val labelTextSize =
        context.resources.getDimension(R.dimen.tabular_form_time_label_text_size)
    private val labelTextColor = Color.WHITE

    private val textStartX = labelWidth + labelPadding * 2

    private val textPaint = Paint().apply {
        color = labelTextColor
        isAntiAlias = true
        textSize = labelTextSize
    }

    private val textBackgroundPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL_AND_STROKE
        color = lineColor
        val cornerPathEffect = CornerPathEffect(6f)
        pathEffect = cornerPathEffect
    }

    private val dateFormat: DateFormat = DateFormat("HH:mm")

    private val textHeightHalf =
        Rect().apply {
            textPaint.getTextBounds("00:00", 0, "00:00".length - 1, this)
        }.height() / 2

    private val textWidth =
        Rect().apply { textPaint.getTextBounds("00:00", 0, "00:00".length, this) }.width()

    class TimeText(private val time: Long, val text: String) {
        fun isValid(currentTime: Long): Boolean {
            return time + 10_000 > currentTime
        }
    }
    private var timeTextCache: TimeText? = null

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val currentTime = System.currentTimeMillis()
        val timeText = timeTextCache?.let {
            if (it.isValid(currentTime)) {
                it.text
            } else {
                null
            }
        } ?: dateFormat
            .format(DateTimeTz.fromUnixLocal(currentTime).addOffset(9.hours))
            .also { timeTextCache = TimeText(currentTime, it) }

        val height = calcLineHeight(parent, currentTime)
        if (height < roomLabelHeight) return

        c.drawLine(textStartX, height, parent.right.toFloat(), height, lineCurrentTimePaint)
        drawBackgroundShape(c, height)
        c.drawText(timeText, textStartX, height + textHeightHalf, textPaint)
    }

    private fun drawBackgroundShape(c: Canvas, height: Float) {
        // TODO: apply correct design. Please check out.

        val path = Path().apply {
            moveTo(labelWidth, height)
            lineTo(labelWidth + labelPadding,
                height - textHeightHalf - labelPadding)
            lineTo(textStartX + textWidth + labelPadding * 2,
                height - textHeightHalf - labelPadding)
            lineTo(textStartX + textWidth + labelPadding * 2,
                height + textHeightHalf + labelPadding)
            lineTo(labelWidth + labelPadding,
                height + textHeightHalf + labelPadding)
            lineTo(labelWidth, height)
        }
        c.drawPath(path, textBackgroundPaint)
    }
}
