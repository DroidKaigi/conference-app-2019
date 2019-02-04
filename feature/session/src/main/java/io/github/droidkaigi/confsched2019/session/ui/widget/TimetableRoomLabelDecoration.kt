package io.github.droidkaigi.confsched2019.session.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import io.github.droidkaigi.confsched2019.model.Room
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.ui.item.TabularServiceSessionItem
import io.github.droidkaigi.confsched2019.session.ui.item.TabularSpacerItem
import io.github.droidkaigi.confsched2019.session.ui.item.TabularSpeechSessionItem

class TimetableRoomLabelDecoration(
    private val columnWidth: Int,
    private val labelHeight: Float,
    private val labelTextSize: Float,
    private val labelBackgroundColor: Int,
    private val labelTextColor: Int,
    private val groupAdapter: GroupAdapter<*>
) : RecyclerView.ItemDecoration() {

    constructor(context: Context, groupAdapter: GroupAdapter<*>) : this(
        context.resources.getDimensionPixelOffset(R.dimen.tabular_form_column_width),
        context.resources.getDimension(R.dimen.tabular_form_room_label_height),
        context.resources.getDimension(R.dimen.tabular_form_room_label_text_size),
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

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        // draw background
        c.drawRect(
            Rect(0, parent.top, parent.width, parent.top + labelHeight.toInt()),
            backgroundPaint
        )

        val rooms = (0 until groupAdapter.itemCount)
            .mapNotNull {
                when (val item = groupAdapter.getItem(it)) {
                    is TabularServiceSessionItem -> item.session.room
                    is TabularSpeechSessionItem -> item.session.room
                    else -> null
                }
            }
            .distinct()
            .sortedBy { it.sequentialNumber }
        val leftView = parent.children.minBy { it.left } ?: return
        val childAdapterPosition = parent.getChildAdapterPosition(leftView)
        if (childAdapterPosition == RecyclerView.NO_POSITION) return
        val leftRoom =
            when (val item = groupAdapter.getItem(childAdapterPosition)) {
                is TabularServiceSessionItem -> item.session.room
                is TabularSpeechSessionItem -> item.session.room
                is TabularSpacerItem -> item.room
                else -> return
            }
        var offsetX = leftView.left
        rooms.firstOrNull { it.sequentialNumber == leftRoom.sequentialNumber - 1 }
            ?.let { drawRoomNumber(c, parent, offsetX - columnWidth, it) }
        for (room in rooms) {
            if (room.sequentialNumber < leftRoom.sequentialNumber) continue
            drawRoomNumber(c, parent, offsetX, room)
            offsetX += columnWidth
            if (offsetX > c.width) break
        }
        for (room in rooms) {
            if (room.sequentialNumber >= leftRoom.sequentialNumber) continue
            drawRoomNumber(c, parent, offsetX, room)
            offsetX += columnWidth
            if (offsetX > c.width) break
        }
    }

    private fun drawRoomNumber(
        c: Canvas,
        parent: RecyclerView,
        offsetX: Int,
        room: Room
    ) {
        val roomName = room.name
        val rect = Rect(
            offsetX,
            parent.top,
            (offsetX + columnWidth),
            parent.top + labelHeight.toInt()
        )
        val baseX = rect.centerX().toFloat() - textPaint.measureText(roomName) / 2f
        val baseY =
            rect.centerY() - (textPaint.fontMetrics.ascent +
                textPaint.fontMetrics.descent) / 2f
        c.drawText(roomName, baseX, baseY, textPaint)
    }
}
