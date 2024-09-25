package stupidrepo.classuncharted.settings

import android.content.Context
import stupidrepo.classuncharted.settings.models.MyCategories
import stupidrepo.classuncharted.settings.models.Setting
import stupidrepo.classuncharted.utils.AuthUtils

class RequireAuthSetting : Setting(MyCategories.SECURITY, "require_auth", true) {
    override fun onUIChangedValue(context: Context, newValue: Any) {
        if(newValue == false) {
            AuthUtils.showBiometricPrompt(context, {
                value = newValue
            },"Verify it's you", "Please verify it's you to turn off authentication.")

            return
        }

        value = newValue

        super.onUIChangedValue(context, newValue)
    }
}