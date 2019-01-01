package io.github.droidkaigi.confsched2019.about.ui.item

import com.xwray.groupie.Item
import com.xwray.groupie.Section
import io.github.droidkaigi.confsched2019.about.fixeddata.AboutThisApp

class AboutSection : Section() {

    fun updateAboutThisApps(
        aboutThisApps: List<AboutThisApp>
    ) {
        val headItem = aboutThisApps.first { it is AboutThisApp.HeadItem } as AboutThisApp.HeadItem
        val header = AboutHeaderItem(
            headItem
        )
        val list = mutableListOf<Item<*>>(header)
        aboutThisApps.filter { it is AboutThisApp.Item }
            .mapTo(list) {
                AboutItem(it as AboutThisApp.Item)
            }
        update(list)
    }
}
