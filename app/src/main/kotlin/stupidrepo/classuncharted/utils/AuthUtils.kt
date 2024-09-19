package stupidrepo.classuncharted.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import stupidrepo.classuncharted.MyApplication
import stupidrepo.classuncharted.settings.RequireAuthSetting

object AuthUtils {
    private fun makeBiometricPrompt(context: Context, onAuthed: () -> Unit): BiometricPrompt {
        return BiometricPrompt(context as FragmentActivity, context.mainExecutor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Toast.makeText(context.applicationContext, "An error occurred: $errString", Toast.LENGTH_SHORT).show()

                if(errorCode == BiometricPrompt.ERROR_HW_NOT_PRESENT || errorCode == BiometricPrompt.ERROR_NO_BIOMETRICS || errorCode == BiometricPrompt.ERROR_HW_UNAVAILABLE) {
                    DialogUtils.showErrorDialog(context, "A strange issue occurred with the authentication hardware. This request has been automatically authenticated.")
                    onAuthed()
                }
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onAuthed()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(context.applicationContext, "Authentication failed!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showBiometricPrompt(prompt: BiometricPrompt, title: String, subtitle: String) {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG
                    or BiometricManager.Authenticators.BIOMETRIC_WEAK
                    or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .build()
        prompt.authenticate(promptInfo)
    }

    fun showBiometricPrompt(
        context: Context,
        onAuthed: () -> Unit,
        title: String,
        subtitle: String,
        negativeButtonText: String = "Cancel"
    ) {
        val settingsManager = (context.applicationContext as MyApplication).SettingsManager
        val shouldPrompt = (settingsManager.getSetting(RequireAuthSetting::class)?.value ?: true) as Boolean

        Log.i("AuthUtils", "showBiometricPrompt: shouldPrompt = $shouldPrompt")

        if(!shouldPrompt) {
            onAuthed()
            return
        }

        if(isSupported(context)) {
            val prompt = makeBiometricPrompt(context, onAuthed)
            showBiometricPrompt(prompt, title, subtitle)

            return
        }

        Toast.makeText(context, "Biometric authentication is not supported on this device.", Toast.LENGTH_SHORT).show()
        onAuthed()
    }

    fun isSupported(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)
        return biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL) == BiometricManager.BIOMETRIC_SUCCESS
    }
}