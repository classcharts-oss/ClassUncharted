package stupidrepo.classuncharted.settings.models

import stupidrepo.classuncharted.settings.RequireAuthSetting
import stupidrepo.classuncharted.settings.ZoomControlsSetting

object MySettings {
    val SETTINGS = listOf(
        RequireAuthSetting(),
        ZoomControlsSetting()
    )
}