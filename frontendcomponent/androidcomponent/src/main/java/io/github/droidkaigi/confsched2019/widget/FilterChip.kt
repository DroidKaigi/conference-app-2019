/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.droidkaigi.confsched2019.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Paint.Style.STROKE
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.M
import android.text.Layout.Alignment.ALIGN_NORMAL
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import android.view.animation.AnimationUtils
import android.widget.Checkable
import androidx.annotation.ColorInt
import androidx.core.animation.doOnEnd
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getDimensionOrThrow
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.res.getDrawableOrThrow
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.withScale
import androidx.core.graphics.withTranslation
import com.google.android.material.math.MathUtils.lerp
import io.github.droidkaigi.confsched2019.widget.component.R

/**
 * A custom view for displaying filters. Allows a custom presentation of the tag color and selection
 * state.
 */
class FilterChip @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), Checkable {

    @ColorInt var color: Int = 0
        set(value) {
            if (field != value) {
                field = value
                dotPaint.color = value
                postInvalidateOnAnimation()
            }
        }

    @ColorInt var textColor: Int = 0
        set(value) {
            if (field != value) {
                field = value
                postInvalidateOnAnimation()
            }
        }

    @ColorInt var selectedTextColor: Int? = null
        set(value) {
            if (field != value) {
                field = value
                if (value != null) {
                    clear = clear.mutate().apply {
                        setTint(value)
                    }
                }
                postInvalidateOnAnimation()
            }
        }

    var text: CharSequence = ""
        set(value) {
            field = value
            updateContentDescription()
            requestLayout()
        }

    var typeface: Typeface?
        get() = textPaint.typeface
        set(value) {
            textPaint.typeface = value
            requestLayout()
        }

    var showIcons: Boolean = true
        set(value) {
            if (field != value) {
                field = value
                requestLayout()
            }
        }

    var onCheckedChangeListener: OnCheckedChangeListener? = null

    private var progress = 0f
        set(value) {
            if (field != value) {
                val prevChecked = isChecked
                field = value
                val newChecked = isChecked
                postInvalidateOnAnimation()
                if (value == 0f || value == 1f) {
                    updateContentDescription()
                }
                if (newChecked != prevChecked) {
                    onCheckedChangeListener?.onCheckedChanged(this, newChecked)
                }
            }
        }

    private val horizontalPadding: Int

    private val verticalPadding: Int

    private val dotPadding: Int

    private val outlinePaint: Paint

    private val textPaint: TextPaint

    private val dotPaint: Paint

    private var clear: Drawable

    private val dotSize: Float

    private val touchFeedback: Drawable

    private lateinit var textLayout: StaticLayout

    private var progressAnimator: ValueAnimator? = null

    private val interp =
        AnimationUtils.loadInterpolator(context, android.R.interpolator.fast_out_slow_in)

    init {
        val a = context.obtainStyledAttributes(
            attrs,
            R.styleable.FilterChip,
            R.attr.filterChipStyle,
            0
        )
        outlinePaint = Paint(ANTI_ALIAS_FLAG).apply {
            color = a.getColorOrThrow(R.styleable.FilterChip_strokeColor)
            strokeWidth = a.getDimensionOrThrow(R.styleable.FilterChip_strokeWidth)
            style = STROKE
        }
        clear = a.getDrawableOrThrow(R.styleable.FilterChip_clearIcon).apply {
            setBounds(
                -intrinsicWidth / 2, -intrinsicHeight / 2, intrinsicWidth / 2, intrinsicHeight / 2
            )
        }
        textColor = a.getColorOrThrow(R.styleable.FilterChip_android_textColor)
        selectedTextColor = a.getColor(R.styleable.FilterChip_selectedTextColor, 0)
        textPaint = TextPaint(ANTI_ALIAS_FLAG).apply {
            color = textColor
            textSize = a.getDimensionOrThrow(R.styleable.FilterChip_android_textSize)
            val font = a.getResourceId(R.styleable.FilterChip_android_fontFamily, 0)
            if (font != 0) {
                typeface = ResourcesCompat.getFont(context, font)
            }
        }
        dotPaint = Paint(ANTI_ALIAS_FLAG)
        color = a.getColor(R.styleable.FilterChip_android_color, 0)
        touchFeedback = a.getDrawableOrThrow(R.styleable.FilterChip_foreground).apply {
            callback = this@FilterChip
        }
        horizontalPadding = a.getDimensionPixelSizeOrThrow(R.styleable.FilterChip_horizontalPadding)
        verticalPadding = a.getDimensionPixelSizeOrThrow(R.styleable.FilterChip_verticalPadding)
        dotPadding = a.getDimensionPixelSizeOrThrow(R.styleable.FilterChip_dotPadding)
        isChecked = a.getBoolean(R.styleable.FilterChip_android_checked, false)
        showIcons = a.getBoolean(R.styleable.FilterChip_showIcons, true)
        dotSize = a.getDimensionOrThrow(R.styleable.FilterChip_dotSize)
        a.recycle()
        clipToOutline = true
        setOnClickListener { toggleWithAnimation() }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val nonTextWidth = 2 * (horizontalPadding + dotPadding) +
            (2 * outlinePaint.strokeWidth).toInt() +
            if (showIcons) maxOf(clear.intrinsicWidth, dotSize.toInt()) else 0
        val nonTextHeight = (2 * verticalPadding) +
            (2 * outlinePaint.strokeWidth).toInt()
        val availableTextWidth = when (MeasureSpec.getMode(widthMeasureSpec)) {
            MeasureSpec.EXACTLY -> MeasureSpec.getSize(widthMeasureSpec) - nonTextWidth
            MeasureSpec.AT_MOST -> MeasureSpec.getSize(widthMeasureSpec) - nonTextWidth
            MeasureSpec.UNSPECIFIED -> Int.MAX_VALUE
            else -> Int.MAX_VALUE
        }
        createLayout(availableTextWidth)
        val w = nonTextWidth + textLayout.textWidth()
        val h = nonTextHeight + textLayout.height
        setMeasuredDimension(w, h)
        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(0, 0, w, h, h / 2f)
            }
        }
        touchFeedback.setBounds(0, 0, w, h)
    }

    override fun onDraw(canvas: Canvas) {
        val strokeWidth = outlinePaint.strokeWidth
        val halfStroke = strokeWidth / 2f
        val rounding = (height - strokeWidth) / 2f

        // Outline
        if (progress < 1f) {
            canvas.drawRoundRect(
                halfStroke,
                halfStroke,
                width - halfStroke,
                height - halfStroke,
                rounding,
                rounding,
                outlinePaint
            )
        }

        // Tag color dot/background
        if (showIcons) {
            val defaultDotRadius = dotSize / 2f
            // Draws beyond bounds and relies on clipToOutline to enforce pill shape
            val dotRadius = lerp(
                defaultDotRadius,
                width.toFloat(),
                progress
            )
            canvas.drawCircle(
                strokeWidth + horizontalPadding + defaultDotRadius,
                height / 2f,
                dotRadius,
                dotPaint
            )
        } else {
            canvas.drawRoundRect(
                halfStroke,
                halfStroke,
                width - halfStroke,
                height - halfStroke,
                rounding,
                rounding,
                dotPaint
            )
        }

        // Text
        val textX = if (showIcons) {
            lerp(
                strokeWidth + horizontalPadding + dotSize + dotPadding,
                strokeWidth + horizontalPadding + dotPadding,
                progress
            )
        } else {
            strokeWidth + horizontalPadding + dotPadding
        }
        val selectedColor = selectedTextColor
        textPaint.color = if (selectedColor != null && selectedColor != 0 && progress > 0) {
            ColorUtils.blendARGB(textColor, selectedColor, progress)
        } else {
            textColor
        }
        canvas.withTranslation(
            x = textX,
            y = (height - textLayout.height) / 2f
        ) {
            textLayout.draw(canvas)
        }

        // Clear icon
        if (showIcons && progress > 0f) {
            canvas.withTranslation(
                x = width - strokeWidth - horizontalPadding - clear.intrinsicWidth / 2,
                y = height / 2f
            ) {
                canvas.withScale(progress, progress) {
                    clear.draw(canvas)
                }
            }
        }

        // Touch feedback
        touchFeedback.draw(canvas)
    }

    /**
     * Starts the animation to enable/disable a filter and invokes a function when done.
     */
    fun animateCheckedAndInvoke(checked: Boolean, onEnd: (() -> Unit)?) {
        val newProgress = if (checked) 1f else 0f
        if (newProgress != progress) {
            progressAnimator?.cancel()
            progressAnimator = ValueAnimator.ofFloat(progress, newProgress).apply {
                addUpdateListener {
                    progress = it.animatedValue as Float
                }
                doOnEnd {
                    progress = newProgress
                    onEnd?.invoke()
                }
                interpolator = interp
                duration = if (checked) SELECTING_DURATION else DESELECTING_DURATION
                start()
            }
        }
    }

    override fun isChecked() = progress == 1f

    override fun toggle() {
        progress = if (isChecked) 0f else 1f
    }

    override fun setChecked(checked: Boolean) {
        progress = if (checked) 1f else 0f
    }

    fun toggleWithAnimation() {
        setCheckedWithAnimation(!isChecked)
    }

    fun setCheckedWithAnimation(checked: Boolean) {
        animateCheckedAndInvoke(checked, null)
    }

    override fun verifyDrawable(who: Drawable): Boolean {
        return super.verifyDrawable(who) || who == touchFeedback
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        touchFeedback.state = drawableState
    }

    override fun jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState()
        touchFeedback.jumpToCurrentState()
    }

    override fun drawableHotspotChanged(x: Float, y: Float) {
        super.drawableHotspotChanged(x, y)
        touchFeedback.setHotspot(x, y)
    }

    private fun createLayout(textWidth: Int) {
        textLayout = if (SDK_INT >= M) {
            StaticLayout.Builder.obtain(text, 0, text.length, textPaint, textWidth).build()
        } else {
            @Suppress("DEPRECATION")
            StaticLayout(text, textPaint, textWidth, ALIGN_NORMAL, 1f, 0f, true)
        }
    }

    private fun updateContentDescription() {
        val desc = if (isChecked) {
            R.string.description_filter_applied
        } else {
            R.string.description_filter_not_applied
        }
        contentDescription = resources.getString(desc, text)
    }

    /**
     * Calculated the widest line in a [StaticLayout].
     */
    private fun StaticLayout.textWidth(): Int {
        var width = 0f
        for (i in 0 until lineCount) {
            width = width.coerceAtLeast(getLineWidth(i))
        }
        return width.toInt()
    }

    interface OnCheckedChangeListener {
        fun onCheckedChanged(chip: FilterChip, isChecked: Boolean)
    }

    companion object {
        private const val SELECTING_DURATION = 350L
        private const val DESELECTING_DURATION = 200L
    }
}

fun FilterChip.onCheckedChanged(action: (FilterChip, Boolean) -> Unit) {
    onCheckedChangeListener = object : FilterChip.OnCheckedChangeListener {
        override fun onCheckedChanged(chip: FilterChip, isChecked: Boolean) {
            action(chip, isChecked)
        }
    }
}
