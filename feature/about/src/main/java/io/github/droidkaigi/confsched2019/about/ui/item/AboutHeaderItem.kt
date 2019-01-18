package io.github.droidkaigi.confsched2019.about.ui.item

import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2019.about.R
import io.github.droidkaigi.confsched2019.about.databinding.HeaderAboutBinding
import io.github.droidkaigi.confsched2019.system.actioncreator.ActivityActionCreator

class AboutHeaderItem(
    val activityActionCreator: ActivityActionCreator
) : BindableItem<HeaderAboutBinding>() {

    override fun getLayout(): Int = R.layout.header_about

    // TODO: Set banner image/background
    override fun bind(binding: HeaderAboutBinding, position: Int) {
        binding.aboutThisAppTwitter.setOnClickListener {
            activityActionCreator.openUrl("https://twitter.com/droidkaigi")
        }
        binding.aboutThisAppGithub.setOnClickListener {
            activityActionCreator.openUrl("https://github.com/DroidKaigi/conference-app-2019")
        }
        binding.aboutThisAppYoutube.setOnClickListener {
            activityActionCreator.openUrl(
                "https://www.youtube.com/channel/UCgK6L-PKx2OZBuhrQ6mmQZw"
            )
        }
        binding.aboutThisAppMedium.setOnClickListener {
            activityActionCreator.openUrl("https://medium.com/droidkaigi")
        }
    }
}
