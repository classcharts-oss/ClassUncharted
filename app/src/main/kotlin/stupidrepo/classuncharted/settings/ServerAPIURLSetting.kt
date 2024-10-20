package stupidrepo.classuncharted.settings

import android.content.Context
import stupidrepo.classuncharted.settings.models.MyCategories
import stupidrepo.classuncharted.settings.models.Setting

class ServerAPIURLSetting : Setting(MyCategories.SECURITY, "server_api_url", "https://www.classcharts.com/apiv2student/") {
    override fun onUIChangedValue(context: Context, newValue: Any) {
        value = newValue
        super.onUIChangedValue(context, newValue)
    }
}