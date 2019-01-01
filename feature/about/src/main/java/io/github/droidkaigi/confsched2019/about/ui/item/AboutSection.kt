package io.github.droidkaigi.confsched2019.about.ui.item

import com.xwray.groupie.Item
import com.xwray.groupie.Section
import io.github.droidkaigi.confsched2019.model.About

class AboutSection : Section() {

    fun updateAboutThisApps(
        aboutThisApps: List<About>
    ) {
        val headItem = aboutThisApps.first { it is About.HeadItem } as About.HeadItem
        val header = AboutHeaderItem(
            headItem
        )
        val list = mutableListOf<Item<*>>(header)
        aboutThisApps.filter { it is About.Item }
            .mapTo(list) {
                AboutItem(it as About.Item)
            }
        update(list)
    }
}
