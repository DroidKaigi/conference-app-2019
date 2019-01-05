package io.github.droidkaigi.confsched2019.system.actioncreator

import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentActivity
import javax.inject.Inject

class ActivityActionCreator @Inject constructor(val activity: FragmentActivity) {
    fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        activity.startActivity(intent)
    }
}
