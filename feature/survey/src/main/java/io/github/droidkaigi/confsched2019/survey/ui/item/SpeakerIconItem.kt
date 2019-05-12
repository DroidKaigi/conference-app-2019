package io.github.droidkaigi.confsched2019.survey.ui.item

import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.squareup.picasso.Picasso
import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2019.model.Speaker
import io.github.droidkaigi.confsched2019.survey.R
import io.github.droidkaigi.confsched2019.survey.databinding.ItemSpeakerIconBinding
import jp.wasabeef.picasso.transformations.CropCircleTransformation

class SpeakerIconItem constructor(
    val speaker: Speaker
) : BindableItem<ItemSpeakerIconBinding>() {

    override fun getLayout(): Int = R.layout.item_speaker_icon

    override fun bind(itemBinding: ItemSpeakerIconBinding, position: Int) {
        itemBinding.speaker = speaker
        val context = itemBinding.icon.context
        val placeHolderColor = ContextCompat.getColor(
            context,
            R.color.colorOnBackgroundSecondary
        )
        val placeHolder = VectorDrawableCompat.create(
            context.resources,
            R.drawable.ic_person_outline_black_24dp,
            null
        )
        placeHolder?.setTint(placeHolderColor)
        speaker.imageUrl?.let { imageUrl ->
            itemBinding.icon.clearColorFilter()
            Picasso.get()
                .load(imageUrl)
                .transform(CropCircleTransformation())
                .apply {
                    placeHolder?.let {
                        placeholder(it)
                    }
                }
                .into(itemBinding.icon)
        } ?: run {
            itemBinding.icon.setImageDrawable(placeHolder)
        }
    }
}
