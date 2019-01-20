package io.github.droidkaigi.confsched2019.session.ui.widget

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.util.SparseArray
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.util.forEach
import androidx.recyclerview.widget.RecyclerView
import com.soywiz.klock.DateTime
import com.soywiz.klock.DateTimeSpan
import com.xwray.groupie.GroupAdapter
import io.github.droidkaigi.confsched2019.model.Session
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.ui.item.SessionItem
import io.github.droidkaigi.confsched2019.timber.debug
import timber.log.Timber

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
    private val lineInterval = resources.getDimensionPixelSize(
        R.dimen.session_bottom_sheet_left_time_line_interval
    ).toFloat()
    // Keep SparseArray instance on property to avoid object creation in every onDrawOver()
    private val adapterPositionToViews = SparseArray<View>()
    private val cachedDateTimeText = HashMap<DateTime, String>()

    private data class TimeText(
        val dayNumber: Int,
        val text: String,
        val y: Float,
        val fixed: Boolean
    )

    private val paint = Paint().apply {
        style = Paint.Style.FILL
        textSize = this@SessionsItemDecoration.textSize.toFloat()
        color = Color.BLACK
        isAntiAlias = true
        try {
            typeface = ResourcesCompat.getFont(context, R.font.lekton)
        } catch (e: Resources.NotFoundException) {
            Timber.debug(e)
        }
    }

    private val dashLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = ContextCompat.getColor(context, R.color.gray3)
        strokeWidth = resources.getDimensionPixelSize(
            R.dimen.session_bottom_sheet_left_time_line_width
        ).toFloat()
        isAntiAlias = true
    }

    private val fontMetrics = paint.fontMetrics
    private val textX = textLeftSpace.toFloat()
    private val lineX = textX + (paint.measureText("00:00") / 2F)

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        // Sort child views by adapter position
        for (i in 0 until parent.childCount) {
            val view = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(view)
            if (position != RecyclerView.NO_POSITION && position < groupAdapter.itemCount) {
                adapterPositionToViews.put(position, view)
            }
        }

        var lastTimeText: TimeText? = null
        adapterPositionToViews.forEach { position, view ->
            val timeText = calcTimeText(position, view) ?: return@forEach

            lastTimeText?.let {
                if (timeText.dayNumber == it.dayNumber && timeText.text == it.text) return@forEach
            }
            lastTimeText = timeText

            c.drawText(
                timeText.text,
                textX,
                timeText.y,
                paint
            )

            // find next time session from all sessions
            var nextTimePosition = -1
            for (pos in position until groupAdapter.itemCount) {
                val session = getSession(pos) ?: break
                if (session.dayNumber != timeText.dayNumber) {
                    break
                }
                val time = extractSessionTime(session)
                if (time != timeText.text) {
                    nextTimePosition = pos
                    break
                }
            }

            // no more different time sessions below, no need to draw line
            if (nextTimePosition < 0) return@forEach

            val offset = if (timeText.fixed) -view.top.toFloat() else 0F
            dashLinePaint.pathEffect = DashPathEffect(
                floatArrayOf(lineInterval, lineInterval),
                offset
            )

            // draw line to next time text if exists, otherwise draw line to the bottom
            calcTimeText(parent, nextTimePosition)?.let { nextTimeText ->
                c.drawLine(
                    lineX,
                    timeText.y + fontMetrics.bottom,
                    lineX,
                    nextTimeText.y + fontMetrics.top,
                    dashLinePaint
                )
            } ?: run {
                c.drawLine(
                    lineX,
                    timeText.y + fontMetrics.bottom,
                    lineX,
                    c.height.toFloat(),
                    dashLinePaint
                )
            }
        }

        adapterPositionToViews.clear()
    }

    private val displayTimezoneOffset = lazy {
        DateTimeSpan(hours = 9).timeSpan // FIXME Get from device setting
    }

    private fun calcTimeText(position: Int, view: View): TimeText? {
        val session = getSession(position) ?: return null
        val nextSession = getSession(position + 1)
        val time = extractSessionTime(session)
        val nextTime = nextSession?.let {
            extractSessionTime(it)
        }

        var y = view.top.coerceAtLeast(0) + textPaddingTop + textSize
        if (session.dayNumber != nextSession?.dayNumber || time != nextTime) {
            y = y.coerceAtMost(view.bottom - textPaddingBottom)
        }
        val fixed = y == textPaddingTop + textSize
        return TimeText(session.dayNumber, time, y.toFloat(), fixed)
    }

    private fun calcTimeText(parent: RecyclerView, position: Int): TimeText? {
        val view = parent.findViewHolderForAdapterPosition(position)?.itemView
            ?: return null
        return calcTimeText(position, view)
    }

    private fun extractSessionTime(session: Session): String {
        return cachedDateTimeText[session.startTime]
            ?: session.startTime.toOffset(displayTimezoneOffset.value)
                .toString("HH:mm").also {
                    cachedDateTimeText[session.startTime] = it
                }
    }

    private fun getSession(position: Int): Session? {
        if (position < 0 || position >= groupAdapter.itemCount) {
            return null
        }
        return (groupAdapter.getItem(position) as? SessionItem)?.session
    }
}
