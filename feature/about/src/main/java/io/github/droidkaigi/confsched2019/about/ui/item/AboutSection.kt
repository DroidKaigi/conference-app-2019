package io.github.droidkaigi.confsched2019.about.ui.item

import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.navigation.Navigation
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.xwray.groupie.Section
import io.github.droidkaigi.confsched2019.about.R
import io.github.droidkaigi.confsched2019.system.actioncreator.ActivityActionCreator
import javax.inject.Inject

class AboutSection @Inject constructor(
    val activity: FragmentActivity,
    val activityActionCreator: ActivityActionCreator
) : Section() {

    fun setupAboutThisApps() {
        update(
            listOf(
                AboutHeaderItem(activityActionCreator),
                AboutItem(
                    name = R.string.about_access_to_place,
                    description = R.string.about_check_map
                ) {
                    activityActionCreator.openVenueOnGoogleMap()
                },
                AboutItem(
                    name = R.string.about_staff_list,
                    description = R.string.about_check
                ) {
                    Toast.makeText(it, "FIXME!!", Toast.LENGTH_SHORT).show()
                },
                AboutItem(
                    name = R.string.about_privacy_policy,
                    description = R.string.about_check
                ) {
                    activityActionCreator.openUrl("http://www.association.droidkaigi.jp/privacy")
                },
                AboutItem(
                    name = R.string.about_license,
                    description = R.string.about_check
                ) {
                    Navigation.findNavController(activity, R.id.root_nav_host_fragment)
                        .navigate(R.id.licenses)
                    OssLicensesMenuActivity.setActivityTitle(it.getString(R.string.about_license))
                },
                AboutItem(
                    name = R.string.about_app_version,
                    description = R.string.version_name_with_commit_hash,
                    isLektonCheckText = true
                )
            )
        )
    }
}
