package stupidrepo.classuncharted.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import stupidrepo.classuncharted.settings.models.Setting

@Composable
fun SettingsCard(
    setting: Setting,
    changeable: @Composable () -> Unit
) {
    val context = LocalContext.current
    val title = context.resources.getIdentifier("settings.setting.${setting.key}", "string", context.packageName)
    val description = context.resources.getIdentifier("settings.setting.${setting.key}.description", "string", context.packageName)

    OutlinedCard(
        modifier = Modifier.padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = title.takeIf { it != 0 }?.let { context.getString(it) } ?: setting.key,
                    style = typography.titleMedium
                )
                Text(
                    text = description.takeIf { it != 0 }?.let { context.getString(it) } ?: "No description found.",
                    style = typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier
                .width(4.dp))

            changeable()
        }
    }
}

@Composable
fun BooleanCard (
    setting: Setting
) {
    SettingsCard(setting) {
        val context = LocalContext.current

        Switch(checked = (setting.value) as Boolean, onCheckedChange = {
            setting.onUIChangedValue(context, it)
        })
    }
}