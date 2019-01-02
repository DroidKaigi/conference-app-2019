package io.github.droidkaigi.confsched2019.sponsor.ui.item

import com.squareup.picasso.Picasso
import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2019.model.Sponsor
import io.github.droidkaigi.confsched2019.sponsor.R
import io.github.droidkaigi.confsched2019.sponsor.databinding.ItemSponsorBinding

class SponsorItem private constructor(
    val sponsor: Sponsor,
    val spanSize: Int,
    val onClick: (url: String) -> Unit
) : BindableItem<ItemSponsorBinding>() {

    companion object {
        fun create(
            sponsor: Sponsor,
            spanSize: Int,
            onClick: (url: String) -> Unit
        ) = SponsorItem(
            sponsor,
            spanSize,
            onClick
        )

        fun createShort(
            sponsor: Sponsor,
            spanSize: Int,
            onClick: (url: String) -> Unit
        ) = ShortSponsorItem(
            sponsor,
            spanSize,
            onClick
        )
    }

    override fun bind(viewBinding: ItemSponsorBinding, position: Int) {
        viewBinding.sponsor = sponsor

        Picasso.get()
            .load(sponsor.image)
            .into(viewBinding.speakerImage)

        viewBinding.card.setOnClickListener {
            onClick(sponsor.url)
        }
    }

    override fun getLayout(): Int = R.layout.item_sponsor

    override fun getSpanSize(spanCount: Int, position: Int): Int {
        return spanSize
    }
}
