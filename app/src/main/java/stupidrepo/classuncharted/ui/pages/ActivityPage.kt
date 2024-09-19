package stupidrepo.classuncharted.ui.pages

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import stupidrepo.classuncharted.data.api.Activity
import stupidrepo.classuncharted.managers.LoginManager
import stupidrepo.classuncharted.ui.composables.ActivityCard
import stupidrepo.classuncharted.ui.composables.CenteredText
import stupidrepo.classuncharted.utils.DialogUtils
import stupidrepo.classuncharted.utils.caching.CacheUtils
import java.time.format.DateTimeFormatter

class ActivityPage : BradPage {
    private val TAG = "ActivityPage"

    private var activities by mutableStateOf(listOf<Activity>())
    private val groupedActivities: Map<String, List<Activity>>
        get() = activities.groupBy { it.format_timestamp.format(DateTimeFormatter.ofPattern("d MMM uuuu")) }

    private var loading by mutableStateOf(true)

    @Composable
    override fun Content(modifier: Modifier, activity: android.app.Activity) {
        val listState = rememberLazyListState()

        LaunchedEffect(listState) {
            snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                .collect { lastIndex ->
                    if (lastIndex != null && lastIndex >= (activities.size + groupedActivities.size) - 1 && !loading && activities.isNotEmpty()) {
                        Log.d(TAG, "Content: Fetching more activities...")
                        fetchMore(lastId = activities.last().id)
                    }
                }
        }

        Column {
            AnimatedVisibility(
                visible = loading,
                modifier = modifier
                    .align(Alignment.CenterHorizontally)
                    .wrapContentSize(Alignment.Center)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .wrapContentSize(Alignment.Center)
                        .padding(8.dp)
                )
            }

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f),
                contentPadding = PaddingValues(8.dp)
            ) {
                if(activities.isEmpty())
                    item {
                        CenteredText("No activities found!", modifier)
                    }
                else
                    groupedActivities.forEach { (date, activities) ->
                        item {
                            CenteredText(
                                text = date,
                                style = typography.bodySmall,
                            )
                        }

                        items(activities.size) { index ->
                            ActivityCard(activity = activities[index])
                        }
                    }
            }
        }
    }

    override fun refresh(activity: android.app.Activity) {
        loading = true

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val ourActivities = LoginManager.user?.getActivities() ?: emptyList()
                activities = ourActivities

                CacheUtils.cacheActivities(ourActivities)
            }
            catch (e: Exception) { DialogUtils.showErrorDialog(activity, e) }
            finally { loading = false }
        }
    }

    private fun fetchMore(lastId: Int) {
        loading = true

        CoroutineScope(Dispatchers.IO).launch {
            val ourActivity = LoginManager.user?.getMoreActivities(lastId)
            if (ourActivity != null) activities += ourActivity

            loading = false
        }
    }
}