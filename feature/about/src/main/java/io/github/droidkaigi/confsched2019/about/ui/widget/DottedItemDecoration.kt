package io.github.droidkaigi.confsched2019.about.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Path
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
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

    private val paint: Paint = Paint().apply {
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

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        if (parent.getChildAdapterPosition(view) < 1) return

        outRect.bottom = width
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = paddingLeft.toFloat()
        val right = (parent.width - paddingRight).toFloat()

        val path = Path()
        parent.forEach { child ->
            val top = (child.bottom + width / 2).toFloat()

            path.moveTo(left, top)
            path.lineTo(right, top)
        }
        c.drawPath(path, paint)
    }
}
