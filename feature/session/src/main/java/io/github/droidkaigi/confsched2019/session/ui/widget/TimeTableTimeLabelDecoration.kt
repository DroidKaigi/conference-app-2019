package io.github.droidkaigi.confsched2019.session.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.soywiz.klock.DateFormat
import com.soywiz.klock.DateTimeTz
import com.soywiz.klock.hours
import com.xwray.groupie.GroupAdapter
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.ui.item.TabularServiceSessionItem
import io.github.droidkaigi.confsched2019.session.ui.item.TabularSpeechSessionItem

// FIXME: TIMEZONE
class TimeTableTimeLabelDecoration(
    private val labelWidth: Float,
    private val labelTextSize: Float,
    private val labelBackgroundColor: Int,
    private val labelTextColor: Int,
    private val groupAdapter: GroupAdapter<*>
) : RecyclerView.ItemDecoration() {

    constructor(context: Context, groupAdapter: GroupAdapter<*>) : this(
        context.resources.getDimension(R.dimen.tabular_form_time_label_width),
        context.resources.getDimension(R.dimen.tabular_form_time_label_text_size),
        Color.WHITE,
        Color.BLACK,
        groupAdapter
    )

    private val backgroundPaint = Paint().apply { color = labelBackgroundColor }

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

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        // draw background
        c.drawRect(
            Rect(0, parent.top, labelWidth.toInt(), parent.bottom),
            backgroundPaint
        )

        // draw time
        parent.children
            .mapNotNull {
                val item = groupAdapter.getItem(parent.getChildAdapterPosition(it))
                val startUnixMillis = when (item) {
                    is TabularServiceSessionItem -> item.session.startTime.unixMillisLong
                    is TabularSpeechSessionItem -> item.session.startTime.unixMillisLong
                    else -> return@mapNotNull null
                }
                it to startUnixMillis
            }
            .distinctBy { (_, time) -> time }
            .forEach { (v, time) ->
                val timeText = dateFormat.format(DateTimeTz.fromUnixLocal(time).addOffset(9.hours))
                val rect =
                    Rect(0, v.top, labelWidth.toInt(), v.top + textHeight)
                val baseX = rect.centerX().toFloat() - textPaint.measureText(timeText) / 2f
                val baseY =
                    rect.centerY() - (textPaint.fontMetrics.ascent +
                        textPaint.fontMetrics.descent) / 2f
                c.drawText(timeText, baseX, baseY, textPaint)
            }
    }
}
