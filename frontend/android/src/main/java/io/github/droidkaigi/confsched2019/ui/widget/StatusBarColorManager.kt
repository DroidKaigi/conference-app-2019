package io.github.droidkaigi.confsched2019.ui.widget

import android.annotation.TargetApi
import android.graphics.Color
import android.os.Build
import android.view.View
import androidx.lifecycle.LiveData
import io.github.droidkaigi.confsched2019.ext.mutableLiveDataOf

class StatusBarColorManager {
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
        // | White theme | Drawer closed   | Drawer opened   |
        // | ----------- | --------------- | --------------- |
        // | No          | White/Invisible | White/Visible   |
        // | Yes         | Black/Invisible | Black/Invisible |

        // Icon color: change based on theme with View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        _systemUiVisibility.value = if (isBrandTheme) {
            0
        } else {
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        // Status bar color
        _statusBarColor.value = if (isBrandTheme && drawerIsOpened) {
            COLOR_STATUS_BAR_VISIBLE
        } else {
            COLOR_STATUS_BAR_INVISIBLE
        }
    }

    private fun updateColorsPreM() {
        // Matrix: icon color / bar visibility
        // | White theme | Drawer closed   | Drawer opened |
        // | ----------- | --------------- | ------------- |
        // | No          | White/Invisible | White/Visible |
        // | Yes         | White/Visible   | White/Visible |

        // Icon color: can not change. Always white

        // Status bar color
        _statusBarColor.value = if (!isBrandTheme || drawerIsOpened) {
            COLOR_STATUS_BAR_VISIBLE
        } else {
            COLOR_STATUS_BAR_INVISIBLE
        }
    }

    companion object {
        private const val DRAWER_OFFSET_OPEN_THRESHOLD = 0.1f
        private const val COLOR_STATUS_BAR_INVISIBLE = Color.TRANSPARENT
        private const val COLOR_STATUS_BAR_VISIBLE = 0x8a000000.toInt()
    }
}
