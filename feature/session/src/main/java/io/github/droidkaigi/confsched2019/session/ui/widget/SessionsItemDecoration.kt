package io.github.droidkaigi.confsched2019.session.ui.widget

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.SparseArray
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.util.forEach
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.ui.item.SessionItem
import io.github.droidkaigi.confsched2019.util.logd

class SessionsItemDecoration(
    val context: Context,
    val groupAdapter: GroupAdapter<*>
) : RecyclerView.ItemDecoration() {
    private val resources = context.resources
    private val textSize = resources.getDimensionPixelSize(
        R.dimen.session_bottom_sheet_left_time_text_size
    )
    private val textLeftSpace = resources.getDimensionPixelSize(
        R.dimen.session_bottom_sheet_left_time_text_left
    )
    private val textPaddingTop = resources.getDimensionPixelSize(
        R.dimen.session_bottom_sheet_left_time_text_padding_top
    )
    private val textPaddingBottom = resources.getDimensionPixelSize(
        R.dimen.session_bottom_sheet_left_time_text_padding_bottom
    )

    val paint = Paint().apply {
        style = Paint.Style.FILL
        textSize = this@SessionsItemDecoration.textSize.toFloat()
        color = Color.BLACK
        isAntiAlias = true
        try {
            typeface = ResourcesCompat.getFont(context, R.font.lekton)
        } catch (e: Resources.NotFoundException) {
            logd(e = e)
        }
    }

    // Keep SparseArray instance on property to avoid object creation in every onDrawOver()
    private val positionToViews = SparseArray<View>()

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        // Sort child views by adapter position
        for (i in 0 until parent.childCount) {
            val view = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(view)
            if (position != -1 && position < groupAdapter.itemCount) {
                positionToViews.put(position, view)
            }
        }

        var lastTime: String? = null
        positionToViews.forEach { position, view ->
            val time = getSessionTime(position) ?: return@forEach

            if (lastTime == time) return@forEach
            lastTime = time

            val nextTime = getSessionTime(position + 1)

            var textY = view.top.coerceAtLeast(0) + textPaddingTop + textSize
            if (time != nextTime) {
                textY = textY.coerceAtMost(view.bottom - textPaddingBottom)
            }

            c.drawText(
                time,
                textLeftSpace.toFloat(),
                textY.toFloat(),
                paint
            )
        }

        positionToViews.clear()
    }

    private fun getSessionTime(position: Int): String? {
        if (position < 0 || position >= groupAdapter.itemCount) {
            return null
        }

        val item = groupAdapter.getItem(position) as? SessionItem ?: return null
        return item.session.startTime.toString("HH:mm")
    }
}
