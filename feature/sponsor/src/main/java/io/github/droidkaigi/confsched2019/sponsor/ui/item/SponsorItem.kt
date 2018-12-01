package io.github.droidkaigi.confsched2019.sponsor.ui.item

import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2019.sponsor.R
import io.github.droidkaigi.confsched2019.sponsor.databinding.ItemSponsorBinding

class SponsorItem() : BindableItem<ItemSponsorBinding>() {

    override fun bind(viewBinding: ItemSponsorBinding, position: Int) {
    }

    override fun getLayout(): Int = R.layout.item_sponsor
}
