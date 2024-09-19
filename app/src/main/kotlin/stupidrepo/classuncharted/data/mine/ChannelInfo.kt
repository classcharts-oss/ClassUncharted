package stupidrepo.classuncharted.data.mine

import android.app.NotificationManager

data class ChannelInfo(
    val id: String = "unknown",
    val name: String = "Unknown Channel",

    val importance: Int = NotificationManager.IMPORTANCE_DEFAULT
)