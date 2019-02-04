package io.github.droidkaigi.confsched2019.session.ui.bindingadapter

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation

// waiting https://stackoverflow.com/questions/53975575/databinding-bindingadapter-in-library-project-is-not-applied
@BindingAdapter(value = ["imageUrl"])
fun loadImage(imageView: ImageView, imageUrl: String?) {
    loadImage(
        imageView = imageView,
        imageUrl = imageUrl,
        circleCrop = false,
        rawPlaceHolder = null,
        placeHolderTint = null,
        listener = null
    )
}

@BindingAdapter(
    value = [
        "imageUrl",
        "circleCrop",
        "placeHolder",
        "placeHolderTint"
    ]
)
fun loadImage(
    imageView: ImageView,
    imageUrl: String?,
    circleCrop: Boolean?,
    rawPlaceHolder: Drawable?,
    placeHolderTint: Int?
) {
    loadImage(
        imageView = imageView,
        imageUrl = imageUrl,
        circleCrop = circleCrop,
        rawPlaceHolder = rawPlaceHolder,
        placeHolderTint = placeHolderTint,
        listener = null
    )
}

@BindingAdapter(
    value = [
        "imageUrl",
        "circleCrop",
        "placeHolder",
        "placeHolderTint",
        "listener"
    ]
)
fun loadImage(
    imageView: ImageView,
    imageUrl: String?,
    circleCrop: Boolean?,
    rawPlaceHolder: Drawable?,
    placeHolderTint: Int?,
    listener: ImageLoadListener?
) {
    val placeHolder = run {
        DrawableCompat.wrap(
            rawPlaceHolder ?: return@run null
        )?.apply {
            setTint(placeHolderTint ?: return@run this)
        }
    }
    imageUrl ?: run {
        imageView.setImageDrawable(placeHolder)
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
        .into(imageView, object : Callback {
            override fun onSuccess() {
                listener?.onImageLoaded()
            }

            override fun onError(e: Exception?) {
                listener?.onImageLoadFailed()
            }
        })
}

/**
 * An interface for responding to image loading completion.
 */
interface ImageLoadListener {
    fun onImageLoaded()
    fun onImageLoadFailed()
}
