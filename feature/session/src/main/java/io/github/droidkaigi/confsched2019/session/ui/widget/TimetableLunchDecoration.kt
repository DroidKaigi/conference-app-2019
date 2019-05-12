package io.github.droidkaigi.confsched2019.session.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import io.github.droidkaigi.confsched2019.model.SessionType
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.ui.item.TabularServiceSessionItem
import java.util.concurrent.TimeUnit

class TimetableLunchDecoration(
    private val timeLabelWidth: Float,
    private val roomLabelHeight: Float,
    private val itemBackgroundColor: Int,
    private val text: String,
    private val itemTextColor: Int,
    private val itemTextSize: Float,
    private val itemPadding: Float,
    private val lineWidth: Float,
    private val lineColor: Int,
    private val itemTextMarginStart: Float,
    private val itemTextMarginTop: Float,
    private val pxPerMinute: Float,
    private val groupAdapter: GroupAdapter<*>
) : RecyclerView.ItemDecoration() {

    constructor(context: Context, groupAdapter: GroupAdapter<*>) : this(
        context.resources.getDimension(R.dimen.tabular_form_time_label_width),
        context.resources.getDimension(R.dimen.tabular_form_room_label_height),
        ContextCompat.getColor(context, R.color.cell_bg_activated),
        context.getString(R.string.session_lunch),
        Color.BLACK,
        context.resources.getDimension(R.dimen.session_item_text_size),
        context.resources.getDimension(R.dimen.session_item_padding),
        context.resources.getDimension(R.dimen.session_item_line_width),
        ContextCompat.getColor(context, R.color.cell_vertical_line),
        context.resources.getDimension(R.dimen.session_item_content_margin_start),
        context.resources.getDimension(R.dimen.session_item_content_margin_top),
        context.resources.getDimension(R.dimen.tabular_form_px_per_minute),
        groupAdapter
    )

    private val backgroundPaint = Paint().apply {
        color = itemBackgroundColor
        isAntiAlias = true
    }

    private val linePaint = Paint().apply {
        color = lineColor
        isAntiAlias = true
    }

    private val textPaint = Paint().apply {
        color = itemTextColor
        isAntiAlias = true
        textSize = itemTextSize
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val lunchItem = getLunchItem() ?: return
        val lunchStartTime = TimeUnit.MILLISECONDS
            .toMinutes(lunchItem.session.startTime.unixMillisLong)
        val lunchEndTime = TimeUnit.MILLISECONDS
            .toMinutes(lunchItem.session.endTime.unixMillisLong)
        val lunchItemHeight = (lunchEndTime - lunchStartTime) * pxPerMinute

        parent.children.mapNotNull {
            val childAdapterPosition = parent.getChildAdapterPosition(it)
            if (childAdapterPosition == RecyclerView.NO_POSITION) {
                return@mapNotNull null
            }
            it to groupAdapter.getItem(childAdapterPosition)
        }.find { (_, item) ->
            item.id == lunchItem.id
        }?.let { (itemView, _) ->
            val top = itemView.top + itemPadding
            val rect = Rect(
                timeLabelWidth.toInt(),
                maxOf(top, parent.top + roomLabelHeight).toInt(),
                parent.right,
                maxOf(top + lunchItemHeight, parent.top + roomLabelHeight).toInt()
            )
            val lineRect = Rect(
                rect.left,
                rect.top,
                rect.left + lineWidth.toInt(),
                rect.bottom
            )
            c.drawRect(rect, backgroundPaint)
            c.drawRect(lineRect, linePaint)
            c.drawTextInRect(rect, textPaint, text)
        }
    }

    private fun getLunchItem(): TabularServiceSessionItem? {
        val items = (0 until groupAdapter.itemCount).map {
            groupAdapter.getItem(it)
        }
        return items.find {
            (it as? TabularServiceSessionItem)?.session?.sessionType == SessionType.LUNCH
        } as? TabularServiceSessionItem
    }

    private fun Canvas.drawTextInRect(rect: Rect, textPaint: Paint, text: String) {
        val textBounds = Rect().apply { textPaint.getTextBounds(text, 0, text.length - 1, this) }
        if (textBounds.width() > rect.width() || textBounds.height() > rect.height()) {
            return
        }
        val baseX = rect.left.toFloat() + itemTextMarginStart
        val baseY = rect.top + itemTextMarginTop +
            if (textBounds.height() != 0) textBounds.height().toFloat()
            else -(textPaint.fontMetrics.ascent + textPaint.fontMetrics.descent)
        this.drawText(text, baseX, baseY, textPaint)
    }
}
