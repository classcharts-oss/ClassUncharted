package stupidrepo.classuncharted.ui.activities

import android.os.Bundle
import android.webkit.WebView
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import stupidrepo.classuncharted.MyApplication
import stupidrepo.classuncharted.data.api.Announcement
import stupidrepo.classuncharted.managers.LoginManager
import stupidrepo.classuncharted.managers.SettingsManager
import stupidrepo.classuncharted.settings.ZoomControlsSetting
import stupidrepo.classuncharted.ui.composables.AndroidAnnoyance
import stupidrepo.classuncharted.ui.composables.AttachmentsList
import stupidrepo.classuncharted.utils.DialogUtils
import java.time.format.DateTimeFormatter

class AnnouncementActivity : FragmentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val announcementId = intent.getParcelableExtra<Announcement>("announcement")?.id!!
        var announcement by mutableStateOf<Announcement?>(null)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                announcement.apply {
                    announcement = LoginManager.user?.getAnnouncements()?.find { it.id == announcementId } ?: throw Exception("Announcement not found")
                }
            } catch (e: Exception) {
                DialogUtils.showErrorDialog(this@AnnouncementActivity, e)
            }
        }

        setContent {
            announcement.let {
                AndroidAnnoyance(topBar = {
                    TopAppBar(title = {
                        Column {
                            Text(
                                text = it?.title ?: "Loading...", style = typography.titleMedium,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            if(announcement != null) Text(
                                text = "${it?.format_timestamp?.format(DateTimeFormatter.ofPattern("d MMM uuuu"))} - ${it?.teacher_name}",
                                style = typography.bodySmall
                            )
                        }
                    }, navigationIcon = {
                        IconButton(onClick = { onBackPressedDispatcher.onBackPressed() }) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                        }
                    })
                }) {
                    FullSizeAnnouncement(it?: return@AndroidAnnoyance)
                }
            }
        }
    }
}

@Composable
fun FullSizeAnnouncement(announcement: Announcement) {
    val settingsManager: SettingsManager = (LocalContext.current.applicationContext as MyApplication).SettingsManager
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
        AndroidView(factory = {
            WebView(it).apply {
                settings.builtInZoomControls = true
                settings.displayZoomControls = (settingsManager.getSetting(ZoomControlsSetting::class)?.value ?: false) as Boolean
            }
        }, update = {
            it.loadData(announcement.description, "text/html", "UTF-8")
        }, modifier = Modifier.weight(1f))

        if (announcement.attachments.isNotEmpty()) {
            Spacer(modifier = Modifier.padding(8.dp))
            AttachmentsList(announcement.attachments)
        }
    }
}