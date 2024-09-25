package stupidrepo.classuncharted.settings

import android.content.Context
import android.content.Intent
import stupidrepo.classuncharted.settings.models.MyCategories
import stupidrepo.classuncharted.settings.models.Setting

class DisableServiceSetting : Setting(MyCategories.GENERAL, "disable_service", false) {
    override fun onUIChangedValue(context: Context, newValue: Any) {
        value = newValue
        super.onUIChangedValue(context, newValue)

        context.applicationContext.packageManager.getLaunchIntentForPackage(context.applicationContext.packageName)?.let {
            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(it)
        }
    }
}