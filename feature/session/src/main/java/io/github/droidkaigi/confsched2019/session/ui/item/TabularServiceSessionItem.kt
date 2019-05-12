package io.github.droidkaigi.confsched2019.session.ui.item

import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2019.item.EqualableContentsProvider
import io.github.droidkaigi.confsched2019.model.ServiceSession
import io.github.droidkaigi.confsched2019.model.defaultLang
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.databinding.ItemTabularServiceSessionBinding
import io.github.droidkaigi.confsched2019.session.ui.actioncreator.SessionContentsActionCreator

class TabularServiceSessionItem @AssistedInject constructor(
    @Assisted override val session: ServiceSession,
    @Assisted private val navDirections: NavDirections,
    private val navController: NavController,
    private val sessionContentsActionCreator: SessionContentsActionCreator
) : BindableItem<ItemTabularServiceSessionBinding>(
    session.id.hashCode().toLong()
), SessionItem, EqualableContentsProvider {

    @AssistedInject.Factory
    interface Factory {
        fun create(
            session: ServiceSession,
            navDirections: NavDirections
        ): TabularServiceSessionItem
    }

    override fun bind(viewBinding: ItemTabularServiceSessionBinding, position: Int) {
        val serviceSession = session
        viewBinding.apply {
            val onClickListener: ((View) -> Unit)? = if (serviceSession.sessionType.supportDetail) {
                {
                    navController.navigate(navDirections)
                }
            } else {
                null
            }
            root.setOnClickListener(onClickListener)
            root.setOnLongClickListener {
                if (serviceSession.sessionType.isFavoritable) {
                    sessionContentsActionCreator.toggleFavorite(serviceSession)
                }
                true
            }
            session = serviceSession
            lang = defaultLang()
            if (serviceSession.isFavorited) {
                backgroundView.setBackgroundColor(
                    ContextCompat.getColor(
                        root.context,
                        R.color.cell_bg_red
                    )
                )
                verticalLineView.setBackgroundColor(
                    ContextCompat.getColor(root.context, R.color.cell_text_red)
                )
                sessionTitle.setTextColor(
                    ContextCompat.getColor(root.context, R.color.cell_text_red)
                )
            } else {
                backgroundView.setBackgroundResource(R.drawable.bg_item_tabular)
                verticalLineView.setBackgroundResource(R.drawable.bg_vertical_line)
                sessionTitle.setTextColor(
                    ContextCompat.getColorStateList(root.context, R.color.tabular_session_title)
                )
            }
            root.isActivated = !serviceSession.isFinished
        }
    }

    override fun getLayout() = R.layout.item_tabular_service_session

    override fun providerEqualableContents(): Array<*> = arrayOf(session)

    override fun equals(other: Any?): Boolean {
        return isSameContents(other)
    }

    override fun hashCode(): Int {
        return contentsHash()
    }
}
