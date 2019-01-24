package io.github.droidkaigi.confsched2019.session.ui.item

import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.databinding.ItemTabularSpacerBinding

class TabularSpacerItem : BindableItem<ItemTabularSpacerBinding>() {

    override fun bind(viewBinding: ItemTabularSpacerBinding, pposition: Int) {
        // no op
    }

    override fun getLayout() = R.layout.item_tabular_spacer
}
