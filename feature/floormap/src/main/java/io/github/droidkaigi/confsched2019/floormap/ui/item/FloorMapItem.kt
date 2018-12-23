package io.github.droidkaigi.confsched2019.floormap.ui.item

import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2019.floormap.R
import io.github.droidkaigi.confsched2019.floormap.databinding.ItemFloorMapBinding

class FloorMapItem() : BindableItem<ItemFloorMapBinding>() {

    override fun bind(viewBinding: ItemFloorMapBinding, position: Int) {
    }

    override fun getLayout(): Int = R.layout.item_floor_map
}
