package io.github.droidkaigi.confsched2019.session.ui.item

import androidx.navigation.NavController
import androidx.navigation.NavDirections
import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2019.model.ServiceSession
import io.github.droidkaigi.confsched2019.model.defaultLang
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.databinding.ItemTabularServiceSessionBinding

class TabularServiceSessionItem(
    val session: ServiceSession,
    private val navDirections: NavDirections,
    private val navController: NavController) :
    BindableItem<ItemTabularServiceSessionBinding>(session.id.hashCode().toLong()) {

    override fun bind(viewBinding: ItemTabularServiceSessionBinding, position: Int) {
        viewBinding.apply {
            root.setOnClickListener {
                navController.navigate(navDirections)
            }
            session = this@TabularServiceSessionItem.session
            lang = defaultLang()
        }
    }

    override fun getLayout() = R.layout.item_tabular_service_session

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TabularServiceSessionItem

        if (session != other.session) return false

        return true
    }

    override fun hashCode(): Int {
        return session.hashCode()
    }
}
