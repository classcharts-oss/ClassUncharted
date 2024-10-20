package stupidrepo.classuncharted.ui.pages

import android.app.Activity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import stupidrepo.classuncharted.data.api.Announcement
import stupidrepo.classuncharted.managers.LoginManager
import stupidrepo.classuncharted.ui.composables.AnnouncementCard
import stupidrepo.classuncharted.ui.composables.CenteredText
import stupidrepo.classuncharted.utils.DialogUtils

class AnnouncementsPage : BradPage {
    private val TAG = "AnnouncementsPage"

    private var announcements by mutableStateOf(listOf<Announcement>())

    @Composable
    override fun Content(modifier: Modifier, activity: Activity) {
        LazyColumn(
            contentPadding = PaddingValues(8.dp)
        ) {
            if(announcements.isEmpty())
                item { CenteredText("No announcements found!") }
            else items(announcements.size) { index -> AnnouncementCard(announcements[index]) }
        }
    }

    override fun refresh(activity: Activity) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val ourAnnouncements = LoginManager.user?.getAnnouncements() ?: emptyList()
                announcements = ourAnnouncements
            }
            catch (e: Exception) { DialogUtils.showErrorDialog(activity, e) }
        }
    }
}