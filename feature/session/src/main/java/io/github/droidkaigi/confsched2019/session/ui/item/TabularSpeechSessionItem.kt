package io.github.droidkaigi.confsched2019.session.ui.item

import androidx.navigation.NavController
import androidx.navigation.NavDirections
import com.squareup.inject.assisted.AssistedInject
import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2019.model.SpeechSession
import io.github.droidkaigi.confsched2019.model.defaultLang
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.databinding.ItemTabularSpeechSessionBinding

class TabularSpeechSessionItem(
    val session: SpeechSession,
    private val navDirections: NavDirections,
    private val navController: NavController) :
    BindableItem<ItemTabularSpeechSessionBinding>(session.id.hashCode().toLong()) {

    override fun bind(viewBinding: ItemTabularSpeechSessionBinding, position: Int) {
        viewBinding.apply {
            root.setOnClickListener {
                navController.navigate(navDirections)
            }
            session = this@TabularSpeechSessionItem.session
            lang = defaultLang()
        }
    }

    override fun getLayout() = R.layout.item_tabular_speech_session

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TabularSpeechSessionItem

        if (session != other.session) return false

        return true
    }

    override fun hashCode(): Int {
        return session.hashCode()
    }
}
