package io.github.droidkaigi.confsched2019.session.ui.item

import androidx.navigation.NavController
import androidx.navigation.NavDirections
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
        itemBinding.speakerText.text = speaker.name
        Picasso.get()
            .load(speaker.imageUrl)
            .transform(CropCircleTransformation())
            .into(itemBinding.speakerImage)

        itemBinding.speakerText.setOnClickListener {
            navController.navigate(clickNavDirection)
        }
    }
}
