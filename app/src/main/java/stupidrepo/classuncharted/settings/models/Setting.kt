package stupidrepo.classuncharted.settings.models

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import stupidrepo.classuncharted.MyApplication

open class Setting(val category: Category, val key: String, val defaultValue: Any) {
    var value by mutableStateOf(defaultValue)

    open fun onClick(context: Context, newValue: Any) {
        (context.applicationContext as MyApplication).SettingsManager.saveSetting(this)
    }
}