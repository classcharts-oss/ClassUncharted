package stupidrepo.classuncharted.ui.pages

import android.app.Activity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import stupidrepo.classuncharted.data.api.Detention
import stupidrepo.classuncharted.managers.LoginManager
import stupidrepo.classuncharted.ui.composables.CenteredText
import stupidrepo.classuncharted.ui.composables.DetentionCard
import stupidrepo.classuncharted.utils.DateUtils
import stupidrepo.classuncharted.utils.DialogUtils
import java.time.LocalDateTime

class DetentionsPage : BradPage {
    private val TAG = "DetentionsPage"

    private var detentions by mutableStateOf(listOf<Detention>())

    @Composable
    override fun Content(modifier: Modifier, activity: Activity) {
        if(detentions.isEmpty()) CenteredText("No detentions to display!", modifier)
        else LazyColumn(
            contentPadding = PaddingValues(0.dp, 0.dp),
            modifier = modifier
        ) {
            val now = LocalDateTime.now().toLocalDate()

            val past = detentions.filter { DateUtils.convertAPIDate(it.date, "uuuu-MM-dd'T'HH:mm:ssXXX").isBefore(now) }
            val today = detentions.filter { DateUtils.convertAPIDate(it.date, "uuuu-MM-dd'T'HH:mm:ssXXX").equals(now) }
            val future = detentions.filter { DateUtils.convertAPIDate(it.date, "uuuu-MM-dd'T'HH:mm:ssXXX").isAfter(now) }

            item { CenteredText("Future Detentions", style = typography.bodySmall) }
            if(future.isNotEmpty()) {
                items(future.size) { index -> DetentionCard(detention = future[index]) }
            }

            item { CenteredText("Today's Detentions", style = typography.bodySmall) }
            if(today.isNotEmpty()) {
                items(today.size) { index -> DetentionCard(detention = today[index]) }
            }

            item { CenteredText("Past Detentions", style = typography.bodySmall) }
            if(past.isNotEmpty()) {
                items(past.size) { index -> DetentionCard(detention = past[index]) }
            }
        }
    }

    override fun refresh(activity: Activity) {
        CoroutineScope(Dispatchers.IO).launch {
            try { detentions = LoginManager.user?.getDetentions() ?: emptyList() }
            catch (e: Exception) { DialogUtils.showErrorDialog(activity, e) }
        }
    }
}