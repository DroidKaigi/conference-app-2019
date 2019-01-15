package io.github.droidkaigi.confsched2019.sponsor.ui.item

import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2019.sponsor.R
import io.github.droidkaigi.confsched2019.sponsor.databinding.ItemDividerBinding

class DividerItem : BindableItem<ItemDividerBinding>() {

    override fun getLayout(): Int = R.layout.item_divider

    override fun bind(binding: ItemDividerBinding, position: Int) {
    }
}
