package io.github.droidkaigi.confsched2019.ui

import androidx.annotation.IdRes
import io.github.droidkaigi.confsched2019.R

enum class PageConfiguration(
    val id: Int,
    val isBrandTheme: Boolean = false,
    val hasTitle: Boolean = true,
    val isShowLogoImage: Boolean = false,
    val hideToolbar: Boolean = false
) {
    MAIN(R.id.main, isBrandTheme = true, hasTitle = false, isShowLogoImage = true),
    DETAIL(R.id.session_detail, hasTitle = false, hideToolbar = true),
    SPEAKER(R.id.speaker, hasTitle = false),
    SURVEY(R.id.session_survey, isBrandTheme = true, hasTitle = false),
    OTHER(0);

    operator fun component1() = id
    operator fun component2() = isBrandTheme
    operator fun component3() = hasTitle
    operator fun component4() = isShowLogoImage

    companion object {
        fun getConfiguration(@IdRes id: Int): PageConfiguration {
            return PageConfiguration
                .values()
                .firstOrNull { it.id == id } ?: OTHER
        }
    }
}
