package stupidrepo.classuncharted.managers

import android.content.Context
import android.util.Log
import org.reflections.Reflections
import stupidrepo.classuncharted.settings.models.Category
import stupidrepo.classuncharted.settings.models.MySettings.SETTINGS
import stupidrepo.classuncharted.settings.models.Setting

class SettingsManager(context: Context) {
    private val TAG = "SettingsManager"
    private val reflections = Reflections()

    private val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    fun getSettingsGroupedByCategory(): Map<Category, List<Setting>> {
        return SETTINGS.groupBy { it.category }
    }

    fun saveSetting(setting: Setting) {
        val editor = sharedPreferences.edit()
        val key = setting.key
        when(setting.value) {
            is Boolean -> editor.putBoolean(key, setting.value as Boolean)
            is Int -> editor.putInt(key, setting.value as Int)
            is String -> editor.putString(key, setting.value as String)
            else -> Log.w(TAG, "Tried to save setting $key, but it was not a supported type!")
        }
        editor.apply()
    }

    fun loadSettings() {
        SETTINGS.forEach { setting ->
            val key = setting.key
            val value = when(setting.value) {
                is Boolean -> sharedPreferences.getBoolean(key, setting.value as Boolean)
                is Int -> sharedPreferences.getInt(key, setting.value as Int)
                is String -> sharedPreferences.getString(key, setting.value as String)
                else -> {
                    Log.w(TAG, "Tried to load setting $key, but it was not a supported type!")
                    return@forEach
                }
            }
            setting.value = value ?: setting.value
        }
    }

    fun <T: Any> getSetting(type: T): Setting? {
        val setting = SETTINGS.find { it::class == type }
        if(setting == null) { Log.w(TAG, "Tried to get setting ${type::class.simpleName}, but it was not found!") }

        return setting
    }
}