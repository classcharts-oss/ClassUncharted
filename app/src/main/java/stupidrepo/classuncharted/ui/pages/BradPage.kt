package stupidrepo.classuncharted.ui.pages

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface BradPage {
    @Composable
    fun Content(modifier: Modifier, activity: Activity)

    fun refresh(activity: Activity)
}