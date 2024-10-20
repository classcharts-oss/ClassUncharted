package stupidrepo.classuncharted

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import kotlinx.serialization.json.Json
import stupidrepo.classuncharted.managers.LoginManager
import stupidrepo.classuncharted.managers.SettingsManager

val JSON = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
}

val isContentLoading = mutableStateOf(false)

class MyApplication: Application() {
    val TAG = "MyApplication"

    override fun onCreate() {
        super.onCreate()

        SettingsManager.initialize(this)
        SettingsManager.instance.loadSettings()
        LoginManager.getLoginDetails(this)?.let {
            LoginManager.logInUser(this, it, {}, {})
        }
    }
}

fun isContentLoading(): Boolean {
    return isContentLoading.value
}

fun setContentLoading(value: Boolean) {
   isContentLoading.value = value
}