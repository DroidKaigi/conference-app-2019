package io.github.droidkaigi.confsched2019.session.ui.item

import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.databinding.ItemSessionBinding
import io.github.droidkaigi.confsched2019.session.model.Session

class SessionItem(val session: Session.SpeechSession) : BindableItem<ItemSessionBinding>() {
    override fun getLayout() = R.layout.item_session

    override fun bind(binding: ItemSessionBinding, position: Int) {
        binding.session = session
    }
}