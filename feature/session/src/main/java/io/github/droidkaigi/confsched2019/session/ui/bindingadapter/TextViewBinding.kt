package io.github.droidkaigi.confsched2019.session.ui.bindingadapter

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import jp.wasabeef.picasso.transformations.CropCircleTransformation


fun loadImage(
    textView: TextView,
    imageUrl: String?,
    circleCrop: Boolean?,
    rawPlaceHolder: Drawable?,
    placeHolderTint: Int?,
    widthDp: Int = 16,
    heightDp: Int = 16
) {
    fun setDrawable(drawable: Drawable) {
        val res = textView.context.resources
        val widthPx = (widthDp * res.displayMetrics.density).toInt()
        val heightPx = (heightDp * res.displayMetrics.density).toInt()
        drawable.setBounds(0, 0, widthPx, heightPx)
        textView.setCompoundDrawables(drawable, null, null, null)
    }

    val placeHolder = run {
        DrawableCompat.wrap(
            rawPlaceHolder ?: return@run null
        )?.apply {
            setTint(placeHolderTint ?: return@run this)
        }
    }
    imageUrl ?: run {
        placeHolder?.let {
            setDrawable(it)
        }
    }

    Picasso
        .get()
        .load(imageUrl)
        .apply {
            if (circleCrop == true) {
                transform(CropCircleTransformation())
            }
            if (placeHolder != null) {
                placeholder(placeHolder)
            }
        }
        .into(object : Target {
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                placeHolderDrawable?.let {
                    setDrawable(it)
                }
            }

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
            }

            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                val res = textView.context.resources
                val drawable = BitmapDrawable(res, bitmap)
                setDrawable(drawable)
            }
        })
}
