package io.github.droidkaigi.confsched2019.session.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.ui.item.TabularServiceSessionItem
import io.github.droidkaigi.confsched2019.session.ui.item.TabularSpacerItem
import io.github.droidkaigi.confsched2019.session.ui.item.TabularSpeechSessionItem
import java.util.concurrent.TimeUnit

class CurrentTimeLineDecoration(
    private val labelWidth: Float,
    private val labelHeight: Float,
    private val labelTextSize: Float,
    private val labelBackgroundColor: Int,
    private val labelTextColor: Int,
    private val lineColor: Int,
    private val lineWidth: Float,
    private val dashLineInterval: Float,
    private val pxPerMin: Int,
    private val groupAdapter: GroupAdapter<*>
): RecyclerView.ItemDecoration() {

    constructor(context: Context, groupAdapter: GroupAdapter<*>) : this(
        context.resources.getDimension(R.dimen.tabular_form_time_label_width),
        context.resources.getDimension(R.dimen.tabular_form_room_label_height),
        context.resources.getDimension(R.dimen.tabular_form_time_label_text_size),
        ContextCompat.getColor(context, R.color.colorPrimary),
        Color.WHITE,
        ContextCompat.getColor(context, R.color.colorPrimary),
        context.resources.getDimension(R.dimen.session_bottom_sheet_left_time_line_width),
        context.resources.getDimension(R.dimen.session_bottom_sheet_left_time_line_interval),
        context.resources.getDimensionPixelSize(R.dimen.tabular_form_px_per_minute),
        groupAdapter
    )

    private val line = Paint().apply {
        color = lineColor
        isAntiAlias = true
        pathEffect = DashPathEffect(floatArrayOf(dashLineInterval, dashLineInterval), 0f)
        strokeWidth = lineWidth
        style = Paint.Style.STROKE
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        val currentTime = System.currentTimeMillis()

        val originView = parent.getChildAt(0)
        val originStartUnixMillis = groupAdapter.getItem(parent.getChildAdapterPosition(originView))
            .startUnixMillis

        getOngoingItem(currentTime).apply {
            this ?: return
            val gapHeight = TimeUnit.MILLISECONDS
                .toMinutes(currentTime - originStartUnixMillis)
                .toFloat() * pxPerMin

            val height = originView.top + gapHeight
            if (height < labelHeight) return
            c.drawLine(labelWidth, originView.top + gapHeight, parent.right.toFloat(), originView.top + gapHeight, line)
        }
    }

    private fun getOngoingItem(currentTime: Long): Item<*>? {
        return (0 until groupAdapter.itemCount)
            .map { groupAdapter.getItem(it) }
            .firstOrNull {
                it.startUnixMillis < currentTime && it.endUnixMillis > currentTime
            }
    }

    private inline val Item<*>.startUnixMillis: Long
        get() {
            return when (this) {
                is TabularSpeechSessionItem -> session.startTime.unixMillisLong
                is TabularServiceSessionItem -> session.startTime.unixMillisLong
                is TabularSpacerItem -> startUnixMillis
                else -> 0
            }
        }

    private inline val Item<*>.endUnixMillis: Long
        get() {
            return when (this) {
                is TabularSpeechSessionItem -> session.endTime.unixMillisLong
                is TabularServiceSessionItem -> session.endTime.unixMillisLong
                is TabularSpacerItem -> endUnixMillis
                else -> 0
            }
        }
}
