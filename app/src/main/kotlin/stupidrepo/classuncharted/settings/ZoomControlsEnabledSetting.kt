package stupidrepo.classuncharted.settings

import android.content.Context
import stupidrepo.classuncharted.settings.models.MyCategories
import stupidrepo.classuncharted.settings.models.Setting

class ZoomControlsEnabledSetting : Setting(MyCategories.UI, "zoom_enabled", true) {
    override fun onUIChangedValue(context: Context, newValue: Any) {
        value = newValue
        super.onUIChangedValue(context, newValue)
    }
}