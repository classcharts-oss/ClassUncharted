package stupidrepo.classuncharted.settings.models

import stupidrepo.classuncharted.settings.RequireAuthSetting
import stupidrepo.classuncharted.settings.ZoomEnabledSetting

object MySettings {
    val SETTINGS = listOf(
        RequireAuthSetting(),
        ZoomEnabledSetting()
    )
}