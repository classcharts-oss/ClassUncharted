package stupidrepo.classuncharted

import android.app.Application
import stupidrepo.classuncharted.managers.SettingsManager
import stupidrepo.classuncharted.utils.caching.CacheUtils

class MyApplication: Application() {
    val TAG = "MyApplication"
    val SettingsManager: SettingsManager by lazy { SettingsManager(this) }

    override fun onCreate() {
        super.onCreate()

        CacheUtils.initCache(this)
        SettingsManager.loadSettings()
    }
}