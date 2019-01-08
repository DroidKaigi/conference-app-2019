package io.github.droidkaigi.confsched2019.about.ui.item

import android.app.Activity
import android.widget.Toast
import androidx.navigation.Navigation
import com.xwray.groupie.Section
import io.github.droidkaigi.confsched2019.about.R

class AboutSection : Section() {

    fun setupAboutThisApps() {
        update(
            listOf(
                AboutHeaderItem(),
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
                    if (it is Activity) {
                        Navigation.findNavController(it, R.id.root_nav_host_fragment).navigate(R.id.licenses)
                    }
                },
                AboutItem(
                    R.string.about_app_version,
                    R.string.about_version_name
                )
            )
        )
    }
}
