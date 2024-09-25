package stupidrepo.classuncharted.ui.pages

import android.app.Activity
import androidx.compose.foundation.ExperimentalFoundationApi
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
import stupidrepo.classuncharted.data.api.Homework
import stupidrepo.classuncharted.managers.LoginManager
import stupidrepo.classuncharted.ui.composables.BradTabSwitcher
import stupidrepo.classuncharted.ui.composables.CenteredText
import stupidrepo.classuncharted.ui.composables.HomeworkCard
import stupidrepo.classuncharted.utils.DialogUtils

class HomeworkPage : BradPage {
    private val TAG = "HomeworkPage"

    private var homework by mutableStateOf(listOf<Homework>())
    private var selectedTab by mutableIntStateOf(0)

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Content(modifier: Modifier, activity: Activity) {
        val tabs = listOf(
            HomeActivity.SimpleTabItem("To-do"),
            HomeActivity.SimpleTabItem("Completed")
        )

        if(homework.isEmpty()) CenteredText("That's nice of your teachers; you have no homework!", modifier)
        else LazyColumn(
                contentPadding = PaddingValues(8.dp),
        ) {
            stickyHeader {
                BradTabSwitcher(tabs = tabs, selectedTab = selectedTab) {
                    selectedTab = it
                }
            }

            when (selectedTab) {
                0 -> homework.filter { !it.status.isTicked }.forEach { homework ->
                    item {
                        HomeworkCard(homework)
                    }
                }
                1 -> homework.filter { it.status.isTicked }.forEach { homework ->
                    item {
                        HomeworkCard(homework)
                    }
                }
            }
        }
    }

    override fun refresh(activity: Activity) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val ourHomework = LoginManager.user?.getHomework() ?: emptyList()
                homework = ourHomework.reversed()

//                CacheUtils.cacheHomework(ourHomework)
            }
            catch (e: Exception) { DialogUtils.showErrorDialog(activity, e) }
        }
    }
}