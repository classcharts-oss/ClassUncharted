package stupidrepo.classuncharted.settings

import android.content.Context
import stupidrepo.classuncharted.settings.models.MyCategories
import stupidrepo.classuncharted.settings.models.Setting

class ZoomEnabledSetting : Setting(MyCategories.UI, "zoom_enabled", true) {
    override fun onClick(context: Context, newValue: Any) {
        value = newValue
        super.onClick(context, newValue)
    }
}