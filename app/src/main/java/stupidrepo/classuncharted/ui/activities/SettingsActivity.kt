package stupidrepo.classuncharted.ui.activities

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import stupidrepo.classuncharted.MyApplication
import stupidrepo.classuncharted.ui.composables.AndroidAnnoyance
import stupidrepo.classuncharted.ui.composables.BooleanCard
import stupidrepo.classuncharted.ui.composables.CenteredText
import stupidrepo.classuncharted.ui.composables.SettingsCard

class SettingsActivity : FragmentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val settingsManager = (application as MyApplication).SettingsManager
            val context = LocalContext.current

            AndroidAnnoyance(topBar = {
                CenterAlignedTopAppBar(title = {
                    Text("Settings")
                }, navigationIcon = {
                    IconButton(onClick = {
                        onBackPressedDispatcher.onBackPressed()
                    }) {
                        Icon(Icons.Rounded.ArrowBackIosNew, contentDescription = "Back")
                    }
                })
            }) {
                LazyColumn(modifier = Modifier.padding(8.dp)) {
                    settingsManager.getSettingsGroupedByCategory().forEach { (category, settings) ->
                        val catTitle = context.resources.getIdentifier("settings.category.${category.key}", "string", context.packageName)

                        item {
                            CenteredText(
                                text = catTitle.takeIf { it != 0 }?.let { context.getString(it) } ?: category.key,
                            )
                        }

                        items(settings.size) { index ->
                            val setting = settings[index]
                            when(setting.defaultValue) {
                                is Boolean -> BooleanCard(setting = setting)
                                else -> SettingsCard(setting = setting) {}
                            }
                        }
                    }
                }
            }
        }
    }
}