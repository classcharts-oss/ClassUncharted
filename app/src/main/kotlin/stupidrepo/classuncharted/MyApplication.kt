package stupidrepo.classuncharted

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import kotlinx.serialization.json.Json
import stupidrepo.classuncharted.managers.SettingsManager
import stupidrepo.classuncharted.utils.caching.CacheUtils

val JSON = Json {
    ignoreUnknownKeys = true
}

val isContentLoading = mutableStateOf(false)

class MyApplication: Application() {
    val TAG = "MyApplication"
    val SettingsManager: SettingsManager by lazy { SettingsManager(this) }

    override fun onCreate() {
        super.onCreate()

        CacheUtils.initCache(this)
        SettingsManager.loadSettings()
    }
}

fun isContentLoading(): Boolean {
    return isContentLoading.value
}

fun setContentLoading(value: Boolean) {
   isContentLoading.value = value
}