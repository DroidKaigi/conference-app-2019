package io.github.droidkaigi.confsched2019.about.ui.item

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.navigation.Navigation
import com.xwray.groupie.Section
import io.github.droidkaigi.confsched2019.about.R
import javax.inject.Inject

class AboutSection @Inject constructor(val activity: FragmentActivity) : Section() {

    fun setupAboutThisApps() {
        update(
            listOf(
                AboutHeaderItem(),
                AboutItem(
                    R.string.about_access_to_place,
                    R.string.about_check_map
                ) {
                    openVenueOnGoogleMap(it)
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

    private fun openVenueOnGoogleMap(context: Context) {
        val venueName = context.getString(R.string.venue_place_name)
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse("geo:0,0?q=$LATITUDE_LOCATION+$LONGITUDE_LOCATION($venueName)")
        ).apply {
            setPackage("com.google.android.apps.maps")
        }.let { intent ->
            context.startActivity(intent)
        }
    }

    companion object {
        const val LATITUDE_LOCATION = "35.696065"
        const val LONGITUDE_LOCATION = "139.690426"
    }
}
