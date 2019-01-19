package io.github.droidkaigi.confsched2019.item

import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import io.github.droidkaigi.confsched2019.widget.component.R

class DividerItem : Item<ViewHolder>() {

    override fun getLayout(): Int = R.layout.item_divider

    override fun bind(viewHolder: ViewHolder, position: Int) {
    }
}
