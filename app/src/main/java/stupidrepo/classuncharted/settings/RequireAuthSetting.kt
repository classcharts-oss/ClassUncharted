package stupidrepo.classuncharted.settings

import android.content.Context
import stupidrepo.classuncharted.settings.models.MyCategories
import stupidrepo.classuncharted.settings.models.Setting
import stupidrepo.classuncharted.utils.AuthUtils

class RequireAuthSetting : Setting(MyCategories.GENERAL, "require_auth", true) {
    override fun onClick(context: Context, newValue: Any) {
        if(newValue == false) {
            AuthUtils.showBiometricPrompt(context, {
                value = newValue
            },"Verify it's you", "Please verify it's you to turn off authentication.")

            return
        }

        value = newValue

        super.onClick(context, newValue)
    }
}