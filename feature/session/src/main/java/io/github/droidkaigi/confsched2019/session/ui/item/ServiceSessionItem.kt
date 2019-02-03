package io.github.droidkaigi.confsched2019.session.ui.item

import androidx.navigation.NavController
import androidx.navigation.NavDirections
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2019.item.EqualableContentsProvider
import io.github.droidkaigi.confsched2019.model.ServiceSession
import io.github.droidkaigi.confsched2019.model.defaultLang
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.databinding.ItemServiceSessionBinding
import io.github.droidkaigi.confsched2019.session.ui.actioncreator.SessionContentsActionCreator

class ServiceSessionItem @AssistedInject constructor(
    @Assisted override val session: ServiceSession,
    @Assisted val navDirections: NavDirections,
    @Assisted val hasStartPadding: Boolean,
    val navController: NavController,
    val sessionContentsActionCreator: SessionContentsActionCreator
) : BindableItem<ItemServiceSessionBinding>(
    session.id.hashCode().toLong()
), SessionItem, EqualableContentsProvider {
    val serviceSession get() = session

    @AssistedInject.Factory
    interface Factory {
        fun create(
            session: ServiceSession,
            navDirections: NavDirections,
            hasStartPadding: Boolean
        ): ServiceSessionItem
    }

    override fun bind(viewBinding: ItemServiceSessionBinding, position: Int) {
        with(viewBinding) {
            if (serviceSession.sessionType.supportDetail) {
                root.setOnClickListener {
                    navController.navigate(navDirections)
                }
            } else {
                root.setOnClickListener(null)
                root.isClickable = false
            }
            hasStartPadding = this@ServiceSessionItem.hasStartPadding
            session = serviceSession
            lang = defaultLang()

            val timeInMinutes: Int = serviceSession.timeInMinutes
            timeAndRoom.text = root.context.getString(
                R.string.session_duration_room_format,
                timeInMinutes,
                serviceSession.room.name
            )
            favorite.setOnClickListener { view ->
                // apply state immediately
                viewBinding.session = serviceSession.copy(
                    isFavorited = !serviceSession.isFavorited
                )
                sessionContentsActionCreator.toggleFavorite(serviceSession)
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_service_session

    override fun providerEqualableContents(): Array<*> = arrayOf(session)

    override fun equals(other: Any?): Boolean {
        return isSameContents(other)
    }

    override fun hashCode(): Int {
        return contentsHash()
    }
}
