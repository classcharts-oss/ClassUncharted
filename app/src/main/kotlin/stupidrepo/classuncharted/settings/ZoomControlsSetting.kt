package stupidrepo.classuncharted.settings

import android.content.Context
import stupidrepo.classuncharted.settings.models.MyCategories
import stupidrepo.classuncharted.settings.models.Setting

class ZoomControlsSetting : Setting(MyCategories.UI, "zoom_controls", true) {
    override fun onClick(context: Context, newValue: Any) {
        value = newValue
        super.onClick(context, newValue)
    }
}

class ZoomEnabledSetting : Setting(MyCategories.UI, "zoom_enabled", true) {
    override fun onClick(context: Context, newValue: Any) {
        value = newValue
        super.onClick(context, newValue)
    }
}