package stupidrepo.classuncharted.ui.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebView
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import stupidrepo.classuncharted.MyApplication
import stupidrepo.classuncharted.data.api.Homework
import stupidrepo.classuncharted.managers.LoginManager
import stupidrepo.classuncharted.managers.SettingsManager
import stupidrepo.classuncharted.settings.ZoomEnabledSetting
import stupidrepo.classuncharted.ui.composables.AndroidAnnoyance
import stupidrepo.classuncharted.utils.DialogUtils
import java.net.URLEncoder
import java.time.format.DateTimeFormatter

class HomeworkActivity : FragmentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val homeworkID = intent.getIntExtra("hid", -1)
        var homework by mutableStateOf<Homework?>(null)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                homework.apply {
                    homework = LoginManager.user?.getHomework()?.find { it.id == homeworkID } ?: throw Exception("Homework not found")
                }
            } catch (e: Exception) {
                DialogUtils.showErrorDialog(this@HomeworkActivity, e)
            }
        }

        setContent {
            homework.let {
                AndroidAnnoyance(topBar = {
                    TopAppBar(title = {
                        Column {
                            Text(
                                text = it?.title ?: "Loading...", style = typography.titleMedium,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            if(homework != null) Text(
                                text = "${it?.issue_date?.format(DateTimeFormatter.ofPattern("d MMM uuuu"))} - ${it?.teacher}",
                                style = typography.bodySmall
                            )
                        }
                    }, navigationIcon = {
                        IconButton(onClick = { onBackPressedDispatcher.onBackPressed() }) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                        }
                    })
                }) {
                    FullSizeHomework(it?: return@AndroidAnnoyance)
                }
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun FullSizeHomework(homework: Homework) {
    val settingsManager: SettingsManager = (LocalContext.current.applicationContext as MyApplication).SettingsManager

    // TODO: De-uglify code 💀
    val builder = StringBuilder()
    builder.append("{\"remember_me\":true,\"session_id\":\"${LoginManager.user?.session_id ?: 0}\"}")
    CookieManager.getInstance().setCookie("www.classcharts.com", "student_session_credentials=${URLEncoder.encode(builder.toString(), "utf-8")}")

    AndroidView(factory = {
        WebView(it).apply {
            settings.builtInZoomControls = (settingsManager.getSetting(ZoomEnabledSetting::class)?.value ?: true) as Boolean
            settings.javaScriptEnabled = true
            Log.i("WV", "FullSizeHomework: ${CookieManager.getInstance().getCookie("www.classcharts.com")}")
        }
    }, update = {
        it.loadUrl("https://www.classcharts.com/mobile/student#${LoginManager.user?.id ?: 0},homework")
    }, modifier = Modifier.fillMaxSize())
}