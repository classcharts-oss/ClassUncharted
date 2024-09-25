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
import stupidrepo.classuncharted.data.api.Lesson
import stupidrepo.classuncharted.managers.LoginManager
import stupidrepo.classuncharted.ui.composables.BradTabSwitcher
import stupidrepo.classuncharted.ui.composables.CenteredText
import stupidrepo.classuncharted.ui.composables.LessonCard
import stupidrepo.classuncharted.utils.DialogUtils
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TimetablePage : BradPage {
    private val TAG = "TimetablePage"

    private var lessons by mutableStateOf(listOf<Lesson>())
    private var selectedTab by mutableIntStateOf(-1)

    private val now = LocalDate.now()
    private val startOfWeek = now.minusDays(now.dayOfWeek.value.toLong() - 1)
    private val monToSun = (0..6).map { startOfWeek.plusDays(it.toLong()) }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Content(modifier: Modifier, activity: Activity) {
        val dayTabs = monToSun.map { HomeActivity.SimpleTabItem(it.format(DateTimeFormatter.ofPattern("E, d MMM"))) }

        LazyColumn(
            contentPadding = PaddingValues(8.dp)
        ) {
            stickyHeader {
                BradTabSwitcher(tabs = dayTabs, selectedTab = selectedTab) {
                    selectedTab = it
                    refresh(activity)
                }
            }
            if(lessons.isEmpty()) item { CenteredText("No lessons found for this date!") }
            else items(lessons.size) { index -> LessonCard(lesson = lessons[index]) }
        }
    }

    override fun refresh(activity: Activity) {
        CoroutineScope(Dispatchers.IO).launch {
            if (selectedTab == -1) {
                val today = LocalDate.now()
                selectedTab = monToSun.indexOf(today)
            }

            try { lessons = LoginManager.user?.getLessons(monToSun[selectedTab]) ?: listOf() }
            catch (e: Exception) { DialogUtils.showErrorDialog(activity, e) }
        }
    }
}