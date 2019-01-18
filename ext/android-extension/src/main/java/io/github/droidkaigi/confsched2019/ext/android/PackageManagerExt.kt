package io.github.droidkaigi.confsched2019.ext.android

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Build

fun PackageManager.queryIntentAllActivities(intent: Intent): List<ResolveInfo> {
    val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        PackageManager.MATCH_ALL
    } else {
        PackageManager.GET_RESOLVED_FILTER
    }

    return queryIntentActivities(intent, flag)
}
