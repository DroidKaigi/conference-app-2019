package io.github.droidkaigi.confsched2019.session.ui.item

import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.databinding.ItemDividerBinding

class DividerItem : BindableItem<ItemDividerBinding>() {

    override fun getLayout(): Int = R.layout.item_divider

    override fun bind(binding: ItemDividerBinding, position: Int) {
    }
}
