package io.github.droidkaigi.confsched2019.about.ui.widget

import android.content.Context
import android.graphics.*
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class DottedItemDecoration private constructor(
    private val color: Int,
    private val paddingLeft: Int,
    private val paddingRight: Int,
    private val width: Int,
    private val gap: Int
) : RecyclerView.ItemDecoration() {

    companion object {
        fun from(
            context: Context,
            colorRes: Int,
            paddingLeftRes: Int,
            paddingRightRes: Int,
            widthRes: Int,
            gapRes: Int
        ): DottedItemDecoration {
            val color = ContextCompat.getColor(context, colorRes)
            val paddingLeftPx = context.resources.getDimensionPixelSize(paddingLeftRes)
            val paddingRightPx = context.resources.getDimensionPixelSize(paddingRightRes)
            val widthPx = context.resources.getDimensionPixelSize(widthRes)
            val gapPx = context.resources.getDimensionPixelSize(gapRes)
            return DottedItemDecoration(
                color,
                paddingLeftPx,
                paddingRightPx,
                widthPx,
                gapPx
            )
        }
    }

    private val paint: Paint = Paint()

    init {
        paint.apply {
            color = this@DottedItemDecoration.color
            style = Paint.Style.STROKE
            strokeWidth = this@DottedItemDecoration.width.toFloat()
            pathEffect = DashPathEffect(
                floatArrayOf(
                    this@DottedItemDecoration.gap.toFloat(),
                    this@DottedItemDecoration.gap.toFloat()
                ),
                0f)
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        if (parent.getChildAdapterPosition(view) < 1) return

        outRect.bottom = width
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = paddingLeft.toFloat()
        val right = (parent.width - paddingRight).toFloat()

        val childCount = parent.childCount
        val path = Path()
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val top = (child.bottom + width / 2).toFloat()

            path.moveTo(left, top)
            path.lineTo(right, top)
        }
        c.drawPath(path, paint)
    }
}
