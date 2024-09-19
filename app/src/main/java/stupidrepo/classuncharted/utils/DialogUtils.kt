package stupidrepo.classuncharted.utils

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import stupidrepo.classuncharted.R

object DialogUtils {
    private val possibleErrorTitles = listOf(
        "Uh oh!",
        "💀",
        "Oh crumbs...",
        "Oops!",
        "Red alert, red alert; it's a catastrophe!"
    )

    fun showErrorDialog(context: Context, message: String) {
        CoroutineScope(Dispatchers.Main).launch {
            MaterialAlertDialogBuilder(context)
                .setTitle(possibleErrorTitles.random())
                .setMessage(message)
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .setIcon(R.drawable.ic_activity_negative)
                .show()
        }
    }

    fun showErrorDialog(context: Context, exception: Exception) {
        CoroutineScope(Dispatchers.Main).launch {
            MaterialAlertDialogBuilder(context)
                .setTitle(possibleErrorTitles.random())
                .setMessage(exception.message ?: "An unknown error occurred.")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .setIcon(R.drawable.ic_activity_negative)
                .show()
        }
    }

    fun showDialog(context: Context, title: String, message: String) {
        CoroutineScope(Dispatchers.Main).launch {
            MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }
}