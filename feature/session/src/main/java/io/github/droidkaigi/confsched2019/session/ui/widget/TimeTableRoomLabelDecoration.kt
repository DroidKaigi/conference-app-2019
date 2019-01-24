package io.github.droidkaigi.confsched2019.session.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.ui.item.TabularServiceSessionItem
import io.github.droidkaigi.confsched2019.session.ui.item.TabularSpacerItem
import io.github.droidkaigi.confsched2019.session.ui.item.TabularSpeechSessionItem

class TimeTableRoomLabelDecoration(
    private val columnCount: Int,
    private val labelHeight: Float,
    private val labelTextSize: Float,
    private val labelBackgroundColor: Int,
    private val labelTextColor: Int,
    private val groupAdapter: GroupAdapter<*>
) : RecyclerView.ItemDecoration() {

    constructor(context: Context, columnCount: Int, groupAdapter: GroupAdapter<*>) : this(
        columnCount,
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

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)
        if (position < columnCount) outRect.top = labelHeight.toInt()
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        // draw background
        c.drawRect(
            Rect(parent.left, parent.top, parent.right, parent.top + labelHeight.toInt()),
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
                c.drawTextAtCenter(
                    room.name,
                    Rect(view.left, parent.top, view.right, parent.top + labelHeight.toInt()),
                    textPaint
                )
            }
    }

    private fun Canvas.drawTextAtCenter(text: String, rect: Rect, paint: Paint) {
        val baseX = rect.centerX().toFloat() - paint.measureText(text) / 2f
        val textBounds = Rect().apply { paint.getTextBounds(text, 0, text.length - 1, this) }
        val baseY = rect.centerY() +
            if (textBounds.height() != 0) textBounds.height() / 2f
            else -(paint.fontMetrics.ascent + paint.fontMetrics.descent) / 2f
        drawText(text, baseX, baseY, paint)
    }
}
