package io.github.droidkaigi.confsched2019.session.ui.bindingadapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation

// waiting https://stackoverflow.com/questions/53975575/databinding-bindingadapter-in-library-project-is-not-applied
@BindingAdapter(value = ["app:imageUrl", "app:circleCrop"])
fun loadImage(imageView: ImageView, imageUrl: String?, circleCrop: Boolean?) {
    imageUrl ?: return
    circleCrop ?: return
    if (circleCrop) {
        Picasso.get().load(imageUrl).transform(CropCircleTransformation()).into(imageView)
        return
    }
    Picasso.get().load(imageUrl).into(imageView)
}
