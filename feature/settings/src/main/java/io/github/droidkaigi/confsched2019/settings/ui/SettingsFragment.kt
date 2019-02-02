package io.github.droidkaigi.confsched2019.settings.ui

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import com.google.android.material.snackbar.Snackbar
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2019.di.PageScope
import io.github.droidkaigi.confsched2019.ext.android.changed
import io.github.droidkaigi.confsched2019.settings.R
import io.github.droidkaigi.confsched2019.settings.ui.widget.DaggerPreferenceFragment
import io.github.droidkaigi.confsched2019.system.actioncreator.ActivityActionCreator
import io.github.droidkaigi.confsched2019.system.actioncreator.SystemActionCreator
import io.github.droidkaigi.confsched2019.system.store.SystemStore
import javax.inject.Inject

class SettingsFragment : DaggerPreferenceFragment() {

    @Inject lateinit var systemStore: SystemStore
    @Inject lateinit var systemActionCreator: SystemActionCreator
    @Inject lateinit var activityActionCreator: ActivityActionCreator

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        requireNotNull(findPreference(getString(R.string.pref_key_add_wifi)))
            .setOnPreferenceClickListener {
                true.also {
                    systemActionCreator.registerWifiConfiguration(
                        ssid = getString(R.string.wifi_ssid),
                        password = getString(R.string.wifi_password)
                    )
                }
            }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        systemStore.wifiRegistrationState.changed(this) {
            val isRegistered = it ?: return@changed

            val message = if (isRegistered) {
                getString(R.string.wifi_registered_message)
            } else {
                if (
                    activityActionCreator.copyText(
                        "wifi_password",
                        getString(R.string.wifi_password)
                    )
                ) {
                    getString(
                        R.string.wifi_failed_to_register_message_so_copied,
                        getString(R.string.wifi_ssid)
                    )
                } else {
                    getString(
                        R.string.wifi_failed_to_register_message,
                        getString(R.string.wifi_ssid),
                        getString(R.string.wifi_password)
                    )
                }
            }

            Snackbar.make(requireNotNull(view), message, Snackbar.LENGTH_LONG).show()
        }
    }
}

@Module
abstract class SettingsFragmentModule {

    @Module
    companion object {
        @JvmStatic
        @Provides
        @PageScope
        fun providesLifecycle(settingsFragment: SettingsFragment): Lifecycle {
            return settingsFragment.viewLifecycleOwner.lifecycle
        }
    }
}
