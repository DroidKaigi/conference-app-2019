package io.github.droidkaigi.confsched2019.ui

import androidx.annotation.IdRes
import io.github.droidkaigi.confsched2019.R

enum class PageConfiguration(
    val id: Int,
    val isWhiteTheme: Boolean = true,
    val hasTitle: Boolean = true,
    val isShowLogoImage: Boolean = false
) {
    Main(R.id.main, isWhiteTheme = false, hasTitle = false, isShowLogoImage = true),
    Detail(R.id.session_detail, hasTitle = false),
    Other(0);

    operator fun component1() = id
    operator fun component2() = isWhiteTheme
    operator fun component3() = hasTitle
    operator fun component4() = isShowLogoImage

    companion object {
        fun getConfiguration(@IdRes id: Int): PageConfiguration {
            return PageConfiguration
                .values()
                .firstOrNull { it.id == id } ?: Other
        }
    }
}
