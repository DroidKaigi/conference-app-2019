package io.github.droidkaigi.confsched2019.ui

import androidx.annotation.IdRes
import io.github.droidkaigi.confsched2019.R

enum class ScreenConfiguration(
    val id: Int,
    val isWhiteTheme: Boolean = true,
    val isShowLogoImage: Boolean = false
) {
    Main(R.id.main, isWhiteTheme = false, isShowLogoImage = true),
    Other(0);

    operator fun component1() = id
    operator fun component2() = isWhiteTheme
    operator fun component3() = isShowLogoImage

    companion object {
        fun getConfiguration(@IdRes id: Int): ScreenConfiguration {
            return ScreenConfiguration
                .values()
                .firstOrNull { it.id == id } ?: Other
        }
    }
}
