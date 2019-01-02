package io.github.droidkaigi.confsched2019.sponsor.ui.item

import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2019.sponsor.R
import io.github.droidkaigi.confsched2019.sponsor.databinding.ItemHeaderBinding

class HeaderItem(val title: String) : BindableItem<ItemHeaderBinding>() {

    override fun getLayout(): Int = R.layout.item_header

    override fun bind(binding: ItemHeaderBinding, position: Int) {
        binding.setTitle(title)
    }
}
