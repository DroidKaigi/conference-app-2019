package io.github.droidkaigi.confsched2019.announcement.ui.item

import com.soywiz.klock.DateFormat
import com.soywiz.klock.TimezoneOffset
import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2019.announcement.R
import io.github.droidkaigi.confsched2019.announcement.databinding.ItemAnnouncementBinding
import io.github.droidkaigi.confsched2019.model.Announcement

class AnnouncementItem(
    val announcement: Announcement
) : BindableItem<ItemAnnouncementBinding>() {

    companion object {
        private val dateFormatter = DateFormat("MM.dd HH:mm")
        private val jstOffset = TimezoneOffset(9.0 * 60 * 60 * 1000)
    }

    override fun bind(itemBinding: ItemAnnouncementBinding, position: Int) {
        // TODO: apply new category icon
        itemBinding.categoryIcon.setImageResource(
            when (announcement.type) {
                Announcement.Type.NOTIFICATION -> R.drawable.ic_feed_notification_blue_20dp
                Announcement.Type.ALERT -> R.drawable.ic_feed_alert_amber_20dp
                Announcement.Type.FEEDBACK -> R.drawable.ic_feed_feedback_cyan_20dp
            }
        )

        itemBinding.dateText.text =
            dateFormatter.format(announcement.publishedAt.toOffset(jstOffset))

        itemBinding.titleText.text = announcement.title
        itemBinding.contentText.text = announcement.content
    }

    override fun getLayout(): Int = R.layout.item_announcement
}
