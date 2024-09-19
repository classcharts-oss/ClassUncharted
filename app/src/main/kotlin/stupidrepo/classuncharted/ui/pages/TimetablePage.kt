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
import stupidrepo.classuncharted.data.api.Lesson
import stupidrepo.classuncharted.managers.LoginManager
import stupidrepo.classuncharted.ui.composables.CenteredText
import stupidrepo.classuncharted.ui.composables.LessonCard
import stupidrepo.classuncharted.utils.DialogUtils

class TimetablePage : BradPage {
    private val TAG = "TimetablePage"

    private var lessons by mutableStateOf(listOf<Lesson>())

    @Composable
    override fun Content(modifier: Modifier, activity: Activity) {
        LazyColumn(
            contentPadding = PaddingValues(8.dp)
        ) {
            if(lessons.isEmpty())
                item { CenteredText("No lessons found for today!") }
            else items(lessons.size) { index -> LessonCard(lesson = lessons[index]) }
        }
    }

    override fun refresh(activity: Activity) {
        CoroutineScope(Dispatchers.IO).launch {
            try { lessons = LoginManager.user?.getLessons() ?: emptyList() }
            catch (e: Exception) { DialogUtils.showErrorDialog(activity, e) }
        }
    }
}