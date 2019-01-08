package io.github.droidkaigi.confsched2019.about.ui.item

import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.navigation.Navigation
import com.xwray.groupie.Section
import io.github.droidkaigi.confsched2019.about.R
import javax.inject.Inject

class AboutSection @Inject constructor(val activity: FragmentActivity) : Section() {

    fun setupAboutThisApps(
        openUrl: ((String) -> Unit)
    ) {
        update(
            listOf(
                AboutHeaderItem(
                    "https://twitter.com/droidkaigi",
                    "https://github.com/DroidKaigi/conference-app-2018",
                    "https://www.youtube.com/channel/UCgK6L-PKx2OZBuhrQ6mmQZw",
                    "https://medium.com/droidkaigi",
                    openUrl
                ),
                AboutItem(
                    R.string.about_access_to_place,
                    R.string.about_check_map
                ) {
                    Toast.makeText(it, "FIXME!!", Toast.LENGTH_SHORT).show()
                },
                AboutItem(
                    R.string.about_staff_list,
                    R.string.about_check
                ) {
                    Toast.makeText(it, "FIXME!!", Toast.LENGTH_SHORT).show()
                },
                AboutItem(
                    R.string.about_privacy_policy,
                    R.string.about_check
                ) {
                    Toast.makeText(it, "FIXME!!", Toast.LENGTH_SHORT).show()
                },
                AboutItem(
                    R.string.about_license,
                    R.string.about_check
                ) {
                    Navigation.findNavController(activity, R.id.root_nav_host_fragment)
                        .navigate(R.id.licenses)
                },
                AboutItem(
                    R.string.about_app_version,
                    R.string.about_version_name
                )
            )
        )
    }
}
