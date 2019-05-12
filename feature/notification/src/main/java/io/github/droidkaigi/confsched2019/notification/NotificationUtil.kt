package io.github.droidkaigi.confsched2019.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

fun Context.notificationBuilder(
    channelInfo: NotificationChannelInfo,
    @ColorRes iconColorRes: Int = R.color.colorPrimary
): NotificationCompat.Builder {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        createDefaultNotificationChannel(
            this,
            channelInfo
        )
    }

    return NotificationCompat.Builder(this, channelInfo.channelId).apply {
        color = ContextCompat.getColor(this@notificationBuilder, iconColorRes)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
internal fun createDefaultNotificationChannel(
    context: Context,
    notificationChannelInfo: NotificationChannelInfo
) {
    val notificationManager = context.getSystemService(NotificationManager::class.java)
    val channel = NotificationChannel(
        notificationChannelInfo.channelId,
        notificationChannelInfo.channelName(context),
        NotificationManager.IMPORTANCE_DEFAULT
    )
    notificationManager.createNotificationChannel(channel)
}

object NotificationUtil {
    fun showNotification(
        context: Context,
        title: String,
        text: String,
        pendingIntent: PendingIntent?,
        channelInfo: NotificationChannelInfo = NotificationChannelInfo.DEFAULT,
        @DrawableRes iconRes: Int = R.drawable.ic_notification,
        @ColorRes colorRes: Int = R.color.colorPrimary,
        builder: NotificationCompat.Builder.() -> Unit = {}
    ) {
        val notificationBuilder = context.notificationBuilder(channelInfo, colorRes).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                showBundleNotification(
                    context,
                    title,
                    channelInfo,
                    iconRes,
                    colorRes
                )
                setGroup(channelInfo.channelId)
            }
            setContentTitle(title)
            setContentText(text)
            setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(text)
            )

            if (pendingIntent != null) {
                setContentIntent(pendingIntent)
            }
            setAutoCancel(true)
            setSmallIcon(iconRes)
        }.apply(builder)

        val notificationManagerCompat = NotificationManagerCompat.from(context)
        notificationManagerCompat.notify(text.hashCode(), notificationBuilder.build())
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun showBundleNotification(
        context: Context,
        title: String,
        channelInfo: NotificationChannelInfo,
        @DrawableRes
        iconRes: Int,
        @ColorRes colorRes: Int
    ) {
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        val notification = context.notificationBuilder(channelInfo, colorRes)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .setSummaryText(title)
            )
            .setSmallIcon(iconRes)
            .setGroup(channelInfo.channelId)
            .setGroupSummary(true)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(channelInfo.channelId.hashCode(), notification)
    }
}
