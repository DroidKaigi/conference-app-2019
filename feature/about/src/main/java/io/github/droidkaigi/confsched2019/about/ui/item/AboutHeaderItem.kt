package io.github.droidkaigi.confsched2019.about.ui.item

import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2019.about.R
import io.github.droidkaigi.confsched2019.about.databinding.HeaderAboutBinding

class AboutHeaderItem() : BindableItem<HeaderAboutBinding>() {
    override fun getLayout(): Int = R.layout.header_about

    // TODO: Set banner image/background
    // TODO: Set archives click listener
    override fun bind(binding: HeaderAboutBinding, position: Int) {
    }
}
