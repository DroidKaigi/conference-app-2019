package io.github.droidkaigi.confsched2019.system.actioncreator

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.FragmentActivity
import io.github.droidkaigi.confsched2019.system.R
import javax.inject.Inject

class ActivityActionCreator @Inject constructor(val activity: FragmentActivity) {
    fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        activity.startActivity(intent)
    }

    fun openVenueOnGoogleMap() {
        val venueName = activity.getString(R.string.venue_place_name)
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse("geo:0,0?q=$LATITUDE_LOCATION+$LONGITUDE_LOCATION($venueName)")
        ).apply {
            setPackage("com.google.android.apps.maps")
        }.let { intent ->
            activity.startActivity(intent)
        }
    }

    companion object {
        const val LATITUDE_LOCATION = "35.696065"
        const val LONGITUDE_LOCATION = "139.690426"
    }
}
