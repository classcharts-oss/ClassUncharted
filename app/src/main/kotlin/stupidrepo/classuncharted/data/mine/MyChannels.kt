package stupidrepo.classuncharted.data.mine

import android.app.NotificationManager

object MyChannels {
    val DetentionChannel: ChannelInfo
        get() {
            return ChannelInfo(
                "detention",
                "Detentions",
                NotificationManager.IMPORTANCE_HIGH
            )
        }

    val TimetableChannel: ChannelInfo
        get() {
            return ChannelInfo(
                "timetable",
                "Timetable",
                NotificationManager.IMPORTANCE_DEFAULT
            )
        }

    val ActivityChannel: ChannelInfo
        get() {
            return ChannelInfo(
                "activity",
                "Activity",
                NotificationManager.IMPORTANCE_HIGH
            )
        }

    val AnnouncementsChannel: ChannelInfo
        get() {
            return ChannelInfo(
                "announcements",
                "Announcements",
                NotificationManager.IMPORTANCE_HIGH
            )
        }

    val OtherChannel: ChannelInfo
        get() {
            return ChannelInfo(
                "other",
                "Other",
                NotificationManager.IMPORTANCE_DEFAULT
            )
        }

    val ServiceChannel: ChannelInfo
        get() {
            return ChannelInfo(
                "service",
                "Service",
                NotificationManager.IMPORTANCE_MIN
            )
        }
}