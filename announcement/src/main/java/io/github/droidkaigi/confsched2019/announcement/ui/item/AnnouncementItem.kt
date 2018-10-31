package io.github.droidkaigi.confsched2019.announcement.ui.item

import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2019.announcement.R
import io.github.droidkaigi.confsched2019.announcement.databinding.ItemAnnouncementBinding

class AnnouncementItem() : BindableItem<ItemAnnouncementBinding>() {

    override fun bind(viewBinding: ItemAnnouncementBinding, position: Int) {
    }

    override fun getLayout(): Int = R.layout.item_announcement
}
