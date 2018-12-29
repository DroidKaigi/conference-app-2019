package io.github.droidkaigi.confsched2019.session.ui.widget

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.ui.item.SessionItem
import io.github.droidkaigi.confsched2019.session.ui.item.SpeechSessionItem
import io.github.droidkaigi.confsched2019.util.logd

class SessionsItemDecoration(
    val context: Context,
    val groupAdapter: GroupAdapter<*>
) : RecyclerView.ItemDecoration() {
    private val resources = context.resources
    val textLeftSpace = resources.getDimensionPixelSize(
        R.dimen.session_bottom_sheet_left_time_text_left
    )
    val textMinTop = resources.getDimensionPixelSize(
        R.dimen.session_bottom_sheet_left_time_text_top_min
    )
    val paint = Paint().apply {
        style = Paint.Style.FILL
        textSize = resources.getDimensionPixelSize(
            R.dimen.session_bottom_sheet_left_time_text_size
        ).toFloat()
        color = Color.BLACK
        isAntiAlias = true
        try {
            typeface = ResourcesCompat.getFont(context, R.font.lekton)
        } catch (e: Resources.NotFoundException) {
            logd(e = e)
        }
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        var lastTime: String? = null
        for (i in 0 until parent.childCount) {
            val view = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(view)
            if (position == -1 || position >= groupAdapter.itemCount) return
            val item = groupAdapter.getItem(position)
            val previousItem = if (position - 1 < 0) {
                null
            } else {
                groupAdapter.getItem(position - 1)
            }
            if (item is SessionItem) {
                val time = item.session.startTime.toString("HH:mm")
                val previousTime = if (previousItem is SpeechSessionItem) {
                    previousItem.session.startTime.toString("HH:mm")
                } else {
                    null
                }
                if (lastTime != time) {
                    val viewCenterY = (view.top + view.bottom) / 2
                    val yPosition = if (viewCenterY < textMinTop || time == previousTime) {
                        textMinTop
                    } else {
                        viewCenterY
                    }
                    c.drawText(
                        time,
                        textLeftSpace.toFloat(),
                        yPosition.toFloat(),
                        paint
                    )
                }
                lastTime = time
            }
        }
    }
}
