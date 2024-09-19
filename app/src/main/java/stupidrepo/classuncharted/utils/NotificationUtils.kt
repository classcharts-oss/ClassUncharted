package stupidrepo.classuncharted.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import stupidrepo.classuncharted.data.mine.ChannelInfo
import stupidrepo.classuncharted.data.mine.NotificationInfo

object NotificationUtils {
    fun sendNotification(
        context: Context,
        notification: Notification.Builder,
        id: Int
    ) {
        val notificationManager = context.getSystemService(
            NotificationManager::class.java
        )

        with(notificationManager) {
            notify(id, notification.build())
        }
    }

    fun sendNotification(context: Context,
                         title: String,
                         message: String,
                         channelInfo: ChannelInfo,
                         icon: Int,
                         id: Int) {
        sendNotification(
            context,
            makeNotification(
                context,
                channelInfo,
                NotificationInfo(
                    id,
                    title,
                    message
                )
            ).setSmallIcon(icon),
            id
        )
    }

    fun makeNotification(
        context: Context,
        channelInfo: ChannelInfo,
        notificationInfo: NotificationInfo
    ) : Notification.Builder {
        val notificationManager = context.getSystemService(
            NotificationManager::class.java
        )

        val channel = NotificationChannel(
            channelInfo.id,
            channelInfo.name,
            channelInfo.importance)

        notificationManager.createNotificationChannel(channel)

        return Notification.Builder(context, channelInfo.id)
            .setContentTitle(notificationInfo.title)
            .setContentText(notificationInfo.message)
            .setAutoCancel(true)
            .setWhen(System.currentTimeMillis())
            .setShowWhen(true)
            .setOnlyAlertOnce(true)
    }
}