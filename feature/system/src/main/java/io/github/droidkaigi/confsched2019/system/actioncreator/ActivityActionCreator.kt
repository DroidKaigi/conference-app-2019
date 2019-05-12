package io.github.droidkaigi.confsched2019.system.actioncreator

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import android.provider.CalendarContract.Events
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.coroutineScope
import io.github.droidkaigi.confsched2019.model.CopyText
import io.github.droidkaigi.confsched2019.system.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class ActivityActionCreator @Inject constructor(
    private val dispatcher: Dispatcher,
    private val activity: FragmentActivity
) : CoroutineScope by activity.coroutineScope {
    fun openUrl(url: String) {
        val customTabsIntent = CustomTabsIntent.Builder()
            .setShowTitle(true)
            .enableUrlBarHiding()
            .setToolbarColor(ContextCompat.getColor(activity, R.color.colorBackground))
            .build()

        // block to multiple launch a Activity
        customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        // launch a Custom Tabs Activity
        customTabsIntent.launchUrl(activity, Uri.parse(url))
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

    fun shareUrl(url: String) {
        val builder: ShareCompat.IntentBuilder = ShareCompat.IntentBuilder.from(activity)
        builder.setText(url)
            .setType("text/plain")
            .startChooser()
    }

    fun openCalendar(
        title: String,
        location: String,
        startUnixMillis: Long,
        endUnixMillis: Long
    ) {
        val intent = Intent(Intent.ACTION_INSERT)
            .setData(Events.CONTENT_URI)
            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startUnixMillis)
            .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endUnixMillis)
            .putExtra(Events.TITLE, "DroidKaigi2019: $title")
            .putExtra(Events.EVENT_LOCATION, location)
        activity.startActivity(intent)
    }

    fun copyText(label: String, text: String) {
        launch {
            val cpManager = ContextCompat.getSystemService(activity, ClipboardManager::class.java)

            if (cpManager == null) {
                dispatcher.dispatch(Action.ClipboardChange(CopyText(text, false)))
                return@launch
            }

            val clip: ClipData = ClipData.newPlainText(label, text)
            cpManager.primaryClip = clip

            dispatcher.dispatch(Action.ClipboardChange(CopyText(text, true)))
        }
    }

    companion object {
        const val LATITUDE_LOCATION = "35.696065"
        const val LONGITUDE_LOCATION = "139.690426"
    }
}
