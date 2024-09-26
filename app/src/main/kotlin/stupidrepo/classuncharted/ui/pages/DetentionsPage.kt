package stupidrepo.classuncharted.ui.pages

import android.app.Activity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import stupidrepo.classuncharted.HomeActivity
import stupidrepo.classuncharted.data.api.Detention
import stupidrepo.classuncharted.data.api.DetentionAttended
import stupidrepo.classuncharted.managers.LoginManager
import stupidrepo.classuncharted.ui.composables.BradTabSwitcher
import stupidrepo.classuncharted.ui.composables.CenteredText
import stupidrepo.classuncharted.ui.composables.DetentionCard
import stupidrepo.classuncharted.utils.DialogUtils
import java.time.LocalDateTime

class DetentionsPage : BradPage {
    private val TAG = "DetentionsPage"

    private var detentions by mutableStateOf(listOf<Detention>())
    private var selectedTab by mutableIntStateOf(2)
    private var filteringFor by mutableIntStateOf(0)

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Content(modifier: Modifier, activity: Activity) {
        if (detentions.isEmpty()) CenteredText("No detentions to display!", modifier)
        else LazyColumn(
            contentPadding = PaddingValues(8.dp),
//            modifier = modifier
        ) {
            val now = LocalDateTime.now().toLocalDate()

            val past = detentions.filter {
                it.local_date.isBefore(now)
            }
            val today = detentions.filter {
                it.local_date == now
            }
            val future = detentions.filter {
                it.local_date.isAfter(now)
            }

            val selected = when (selectedTab) {
                1 -> past
                2 -> today
                3 -> future
                else -> detentions
            }

            val final = when (filteringFor) {
                1 -> selected.filter { it.attended == DetentionAttended.yes }
                2 -> selected.filter { it.attended == DetentionAttended.upscaled }
                else -> selected
            }

            val tabs = listOf(
                HomeActivity.SimpleTabItem("All"),
                HomeActivity.SimpleTabItem("Past"),
                HomeActivity.SimpleTabItem("Today"),
                HomeActivity.SimpleTabItem("Future")
            )

            val filters = listOf(
                HomeActivity.SimpleTabItem("All (${selected.size})"),
                HomeActivity.SimpleTabItem("Attended (${selected.count { it.attended == DetentionAttended.yes }})"),
                HomeActivity.SimpleTabItem("Upscaled (${selected.count { it.attended == DetentionAttended.upscaled }})"),
            )

            stickyHeader {
                Column {
                    BradTabSwitcher(tabs = tabs, selectedTab = selectedTab) {
                        selectedTab = it
                    }
                    BradTabSwitcher(tabs = filters, selectedTab = filteringFor) {
                        filteringFor = it
                    }
                }
            }

            if(final.isEmpty()) item { CenteredText("No detentions to display with these filters!", modifier) }
            else final.forEach { detention ->
                item {
                    DetentionCard(detention)
                }
            }
        }
    }

    override fun refresh(activity: Activity) {
        CoroutineScope(Dispatchers.IO).launch {
            try { detentions = LoginManager.user?.getDetentions()?.reversed() ?: emptyList() }
            catch (e: Exception) { DialogUtils.showErrorDialog(activity, e) }
        }
    }
}