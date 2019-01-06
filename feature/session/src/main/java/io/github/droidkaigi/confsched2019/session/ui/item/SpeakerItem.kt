package io.github.droidkaigi.confsched2019.session.ui.item

import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.squareup.picasso.Picasso
import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2019.model.Speaker
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.databinding.ItemSpeakerBinding
import jp.wasabeef.picasso.transformations.CropCircleTransformation

class SpeakerItem @AssistedInject constructor(
    @Assisted val speaker: Speaker,
    @Assisted val clickNavDirection: NavDirections,
    val navController: NavController
) : BindableItem<ItemSpeakerBinding>(speaker.id.hashCode().toLong()) {
    @AssistedInject.Factory
    interface Factory {
        fun create(
            speaker: Speaker,
            clickNavDirection: NavDirections
        ): SpeakerItem
    }

    override fun getLayout(): Int = R.layout.item_speaker

    override fun bind(itemBinding: ItemSpeakerBinding, position: Int) {
        itemBinding.speaker = speaker
        val context = itemBinding.speakerImage.context
        val placeHolderColor = ContextCompat.getColor(
            context,
            R.color.gray2
        )
        val placeHolder = VectorDrawableCompat.create(
            context.resources,
            R.drawable.ic_person_outline_black_24dp,
            null
        )
        placeHolder?.setTint(placeHolderColor)
        speaker.imageUrl?.let { imageUrl ->
            itemBinding.speakerImage.clearColorFilter()
            Picasso.get()
                .load(imageUrl)
                .transform(CropCircleTransformation())
                .apply {
                    placeHolder?.let {
                        placeholder(it)
                    }
                }
                .into(itemBinding.speakerImage)
        } ?: run {
            itemBinding.speakerImage.setImageDrawable(placeHolder)
        }

        itemBinding.root.setOnClickListener {
            navController.navigate(clickNavDirection)
        }
    }
}
