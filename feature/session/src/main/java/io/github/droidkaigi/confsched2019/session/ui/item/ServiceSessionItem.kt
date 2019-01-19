package io.github.droidkaigi.confsched2019.session.ui.item

import androidx.navigation.NavController
import androidx.navigation.NavDirections
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2019.model.Session
import io.github.droidkaigi.confsched2019.model.defaultLang
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.databinding.ItemServiceSessionBinding
import io.github.droidkaigi.confsched2019.session.ui.actioncreator.SessionContentsActionCreator

class ServiceSessionItem @AssistedInject constructor(
    @Assisted override val session: Session.ServiceSession,
    @Assisted val navDirections: NavDirections,
    @Assisted val addPaddingForTime: Boolean,
    navController: NavController,
    sessionContentsActionCreator: SessionContentsActionCreator
) : BindableItem<ItemServiceSessionBinding>(
    session.id.hashCode().toLong()
), SessionItem {
    val serviceSession get() = session

    @AssistedInject.Factory
    interface Factory {
        fun create(
            session: Session.ServiceSession,
            navDirections: NavDirections,
            addPaddingForTime: Boolean
        ): ServiceSessionItem
    }

    private val onFavoriteClickListener: (Session.ServiceSession) -> Unit = { session ->
        sessionContentsActionCreator.toggleFavorite(session)
    }
    private val onClickListener: (Session.ServiceSession) -> Unit = { session ->
        navController
            .navigate(
                navDirections
            )
    }

    override fun bind(viewBinding: ItemServiceSessionBinding, position: Int) {
        with(viewBinding) {
            if (serviceSession.sessionType.supportDetail) {
                root.setOnClickListener { onClickListener(serviceSession) }
            } else {
                root.setOnClickListener(null)
                root.isClickable = false
            }
            addPaddingForTime = this@ServiceSessionItem.addPaddingForTime
            session = serviceSession
            lang = defaultLang()

            val timeInMinutes: Int = serviceSession.timeInMinutes
            timeAndRoom.text = root.context.getString(
                R.string.session_duration_room_format,
                timeInMinutes,
                serviceSession.room.name
            )
            favorite.setOnClickListener {
                onFavoriteClickListener(serviceSession)
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_service_session

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
