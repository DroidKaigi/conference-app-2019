package io.github.droidkaigi.confsched2019.session.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.ui.item.TabularServiceSessionItem
import io.github.droidkaigi.confsched2019.session.ui.item.TabularSpacerItem
import io.github.droidkaigi.confsched2019.session.ui.item.TabularSpeechSessionItem

class TimeTableRoomLabelDecoration(
    private val labelHeight: Float,
    private val labelTextSize: Float,
    private val labelBackgroundColor: Int,
    private val labelTextColor: Int,
    private val groupAdapter: GroupAdapter<*>
) : RecyclerView.ItemDecoration() {

    constructor(context: Context, groupAdapter: GroupAdapter<*>) : this(
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

        // draw room number
        parent.children
            .map { it to groupAdapter.getItem(parent.getChildAdapterPosition(it)) }
            .distinctBy { (view, _) -> view.left }
            .forEach { (view, item) ->
                val room = when (item) {
                    is TabularSpacerItem -> item.room
                    is TabularServiceSessionItem -> item.session.room
                    is TabularSpeechSessionItem -> item.session.room
                    else -> return@forEach
                }

                val roomName = room.name
                val rect =
                    Rect(view.left, parent.top, view.right, parent.top + labelHeight.toInt())
                val baseX = rect.centerX().toFloat() - textPaint.measureText(roomName) / 2f
                val baseY =
                    rect.centerY() - (textPaint.fontMetrics.ascent +
                        textPaint.fontMetrics.descent) / 2f
                c.drawText(roomName, baseX, baseY, textPaint)
            }
    }
}
