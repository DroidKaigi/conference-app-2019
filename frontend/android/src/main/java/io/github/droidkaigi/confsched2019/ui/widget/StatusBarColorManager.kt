package io.github.droidkaigi.confsched2019.ui.widget

import android.annotation.TargetApi
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.view.View
import androidx.lifecycle.LiveData
import io.github.droidkaigi.confsched2019.ext.mutableLiveDataOf

class StatusBarColorManager(
    val context: Context
) {
    private val _systemUiVisibility = mutableLiveDataOf(0)
    val systemUiVisibility: LiveData<Int> = _systemUiVisibility

    private val _statusBarColor =
        mutableLiveDataOf(COLOR_STATUS_BAR_INVISIBLE)
    val statusBarColor: LiveData<Int> = _statusBarColor

    var drawerSlideOffset: Float = 0f
        set(value) {
            if (field != value) {
                field = value
                updateColors()
            }
        }
    private val drawerIsOpened: Boolean
        get() = drawerSlideOffset >= DRAWER_OFFSET_OPEN_THRESHOLD

    var isBrandTheme: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                updateColors()
            }
        }

    private fun updateColors() {
        if (23 <= Build.VERSION.SDK_INT) {
            updateColorsM()
        } else {
            updateColorsPreM()
        }
    }

    @TargetApi(23)
    private fun updateColorsM() {
        // Matrix: icon color / bar visibility
        // | Brand theme | Drawer closed   | Drawer opened   |
        // | ----------- | --------------- | --------------- |
        // | Yes         | White/Invisible | White/Visible   |
        // | No          | Black/Invisible | Black/Invisible |

        // Icon color: change based on theme with View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        _systemUiVisibility.value = if (isBrandTheme || isNightMode()) {
            0
        } else {
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        // Status bar color
        _statusBarColor.value = if ((isBrandTheme || isNightMode()) && drawerIsOpened) {
            COLOR_STATUS_BAR_VISIBLE
        } else {
            COLOR_STATUS_BAR_INVISIBLE
        }
    }

    private fun updateColorsPreM() {
        // Matrix: icon color / bar visibility
        // | Brand theme | Drawer closed   | Drawer opened |
        // | ----------- | --------------- | ------------- |
        // | Yes         | White/Invisible | White/Visible |
        // | No          | White/Visible   | White/Visible |

        // Icon color: can not change. Always white

        // Status bar color
        _statusBarColor.value = if (!(isBrandTheme || isNightMode()) || drawerIsOpened) {
            COLOR_STATUS_BAR_VISIBLE
        } else {
            COLOR_STATUS_BAR_INVISIBLE
        }
    }

    private fun isNightMode(): Boolean {
        val nightModeFlags =
            context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            Configuration.UI_MODE_NIGHT_UNDEFINED -> false
            else -> false
        }
    }

    companion object {
        private const val DRAWER_OFFSET_OPEN_THRESHOLD = 0.1f
        private const val COLOR_STATUS_BAR_INVISIBLE = Color.TRANSPARENT
        private const val COLOR_STATUS_BAR_VISIBLE = 0x8a000000.toInt()
    }
}
