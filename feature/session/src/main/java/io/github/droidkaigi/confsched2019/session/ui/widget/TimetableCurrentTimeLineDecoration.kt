package io.github.droidkaigi.confsched2019.session.ui.widget

import android.content.Context
import android.graphics.Canvas
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

open class TimetableCurrentTimeLineDecoration(
    context: Context,
    val groupAdapter: GroupAdapter<*>
) : RecyclerView.ItemDecoration() {

    protected val labelWidth =
        context.resources.getDimension(R.dimen.tabular_form_time_label_width)
    protected val roomLabelHeight =
        context.resources.getDimension(R.dimen.tabular_form_room_label_height)
    protected val lineColor =
        ContextCompat.getColor(context, R.color.red1)

    private val lineWidth =
        context.resources.getDimension(R.dimen.tabular_form_line_width_bold)
    private val pxPerMin =
        context.resources.getDimensionPixelSize(R.dimen.tabular_form_px_per_minute)

    protected val lineCurrentTimePaint = Paint().apply {
        color = lineColor
        isAntiAlias = true
        strokeWidth = lineWidth
        style = Paint.Style.STROKE
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        val currentTime = System.currentTimeMillis()
        val height = calcLineHeight(parent, currentTime)
        if (height < roomLabelHeight) return

        c.drawLine(labelWidth, height, parent.right.toFloat(), height, lineCurrentTimePaint)
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

    protected fun calcLineHeight(parent: RecyclerView, currentTime: Long): Float {
        val originView = parent.getChildAt(0) ?: return 0F
        val originStartUnixMillis = groupAdapter.getItem(parent.getChildAdapterPosition(originView))
            .startUnixMillis

        val gapHeight = TimeUnit.MILLISECONDS
            .toMinutes(currentTime - originStartUnixMillis)
            .toFloat() * pxPerMin

        return originView.top + gapHeight
    }
}
