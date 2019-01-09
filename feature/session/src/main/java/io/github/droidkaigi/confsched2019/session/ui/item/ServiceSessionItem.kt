package io.github.droidkaigi.confsched2019.session.ui.item

import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2019.model.Session
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.databinding.ItemSpecialSessionBinding

class ServiceSessionItem(
    override val session: Session.ServiceSession
) : BindableItem<ItemSpecialSessionBinding>(
    session.id.hashCode().toLong()
), SessionItem {
    val specialSession get() = session

    override fun bind(viewBinding: ItemSpecialSessionBinding, position: Int) {
        with(viewBinding) {
            session = specialSession

            @Suppress("StringFormatMatches") // FIXME
            timeAndRoom.text = root.context.getString(
                R.string.session_duration_room_format,
                specialSession.timeInMinutes,
                specialSession.room.name
            )
        }
    }

    override fun getLayout(): Int = R.layout.item_special_session

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ServiceSessionItem

        if (session != other.session) return false

        return true
    }

    override fun hashCode(): Int {
        return session.hashCode()
    }
}
