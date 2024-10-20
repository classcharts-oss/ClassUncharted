package stupidrepo.classuncharted.settings.models

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import stupidrepo.classuncharted.managers.SettingsManager

open class Setting(val category: Category, val key: String, val defaultValue: Any) {
    var value by mutableStateOf(defaultValue)

    open fun onUIChangedValue(context: Context, newValue: Any) {
        SettingsManager.instance.saveSetting(this)
    }
}