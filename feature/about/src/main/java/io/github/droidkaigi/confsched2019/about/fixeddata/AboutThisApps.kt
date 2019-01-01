package io.github.droidkaigi.confsched2019.about.fixeddata

import io.github.droidkaigi.confsched2019.about.R

class AboutThisApps {

    companion object {
        fun getThisApps(): List<AboutThisApp> {
            var index = 0
            return listOf(
                // Head Item
                AboutThisApp.HeadItem(
                    10000 + index++,
                    R.string.what_is_droidkaigi,
                    R.string.description_droidkaigi,
                    ""
                ),
                // TODO: Set item links
                // Access Item
                AboutThisApp.Item(
                    10000 + index++,
                    R.string.access_to_place,
                    R.string.check_map,
                    ""
                ),
                // StaffList Item
                AboutThisApp.Item(
                    10000 + index++,
                    R.string.staff_list,
                    R.string.check,
                    ""
                ),
                // Privacy Policy Item
                AboutThisApp.Item(
                    10000 + index++,
                    R.string.privacy_policy,
                    R.string.check,
                    ""
                ),
                // License Item
                AboutThisApp.Item(
                    10000 + index++,
                    R.string.license,
                    R.string.check,
                    ""
                ),
                // App Version Item
                AboutThisApp.Item(
                    10000 + index++,
                    R.string.app_version,
                    R.string.version_name,
                    ""
                )
            )
        }
    }
}
