package io.github.droidkaigi.confsched2019.sponsor.ui.item

import androidx.annotation.LayoutRes
import com.squareup.picasso.Picasso
import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2019.model.Sponsor
import io.github.droidkaigi.confsched2019.sponsor.R
import io.github.droidkaigi.confsched2019.sponsor.databinding.ItemSponsorBinding
import io.github.droidkaigi.confsched2019.sponsor.databinding.ItemSponsorShortBinding


class ShortSponsorItem internal constructor(
    val sponsor: Sponsor,
    val spanSize: Int,
    val onClick: (url: String) -> Unit
) : BindableItem<ItemSponsorShortBinding>() {

    override fun bind(viewBinding: ItemSponsorShortBinding, position: Int) {
        viewBinding.sponsor = sponsor

        Picasso.get()
            .load(sponsor.image)
            .into(viewBinding.speakerImage)

        viewBinding.card.setOnClickListener {
            onClick(sponsor.url)
        }
    }

    override fun getLayout(): Int = R.layout.item_sponsor_short

    override fun getSpanSize(spanCount: Int, position: Int): Int {
        return spanSize
    }
}

