package io.github.droidkaigi.confsched2019.announcement.ui.item

import com.soywiz.klock.DateFormat
import com.soywiz.klock.TimezoneOffset
import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2019.announcement.R
import io.github.droidkaigi.confsched2019.announcement.databinding.ItemAnnouncementBinding
import io.github.droidkaigi.confsched2019.model.Post

class AnnouncementItem(
    val post: Post
) : BindableItem<ItemAnnouncementBinding>() {

    companion object {
        private val dateFormatter = DateFormat("MM.dd HH:mm")
        private val jstOffset = TimezoneOffset(9.0 * 60 * 60 * 1000)
    }

    override fun bind(itemBinding: ItemAnnouncementBinding, position: Int) {
        // TODO: bind category icon
        // itemBinding.categoryIcon

        itemBinding.dateText.text = dateFormatter.format(post.date.toOffset(jstOffset))

        itemBinding.titleText.text = post.title
        itemBinding.contentText.text = post.content
    }

    override fun getLayout(): Int = R.layout.item_announcement
}
