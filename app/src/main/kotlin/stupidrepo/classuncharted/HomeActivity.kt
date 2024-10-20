package stupidrepo.classuncharted

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Announcement
import androidx.compose.material.icons.automirrored.outlined.Announcement
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import stupidrepo.classuncharted.managers.LoginManager
import stupidrepo.classuncharted.ui.activities.SettingsActivity
import stupidrepo.classuncharted.ui.composables.AndroidAnnoyance
import stupidrepo.classuncharted.ui.composables.IconTabSwitcher
import stupidrepo.classuncharted.ui.composables.LoadingIndicator
import stupidrepo.classuncharted.ui.modal.AccountDialog
import stupidrepo.classuncharted.ui.pages.ActivityPage
import stupidrepo.classuncharted.ui.pages.AnnouncementsPage
import stupidrepo.classuncharted.ui.pages.BradPage
import stupidrepo.classuncharted.ui.pages.DetentionsPage
import stupidrepo.classuncharted.ui.pages.HomeworkPage
import stupidrepo.classuncharted.ui.pages.TimetablePage
import stupidrepo.classuncharted.ui.theme.ClassUnchartedTheme

val selectedTabIndex = mutableIntStateOf(0) // we put this here so that when we rotate the screen, the selected tab doesn't change 🙄

class HomeActivity : FragmentActivity() {
    data class SimpleTabItem(
        val title: String,
    )

    data class TabItem(
        val title: String,
        val icon: ImageVector,
        val selectedIcon: ImageVector,
        val page: BradPage
    )

    private val TAG = "HomeActivity"

    private val tabItems = listOf(
        TabItem(
            title = "Detentions",
            icon = Icons.Outlined.AccessTime,
            selectedIcon = Icons.Filled.AccessTimeFilled,
            page = DetentionsPage()
        ),
        TabItem(
            title = "Homework",
            icon = Icons.Outlined.WorkOutline,
            selectedIcon = Icons.Filled.Work,
            page = HomeworkPage()
        ),
        TabItem(
            title = "Timetable",
            icon = Icons.Outlined.Today,
            selectedIcon = Icons.Filled.Today,
            page = TimetablePage()
        ),
        TabItem(
            title = "Announcements",
            icon = Icons.AutoMirrored.Outlined.Announcement,
            selectedIcon = Icons.AutoMirrored.Filled.Announcement,
            page = AnnouncementsPage(),
        ),
        TabItem(
            title = "Activity",
            icon = Icons.Outlined.Assessment,
            selectedIcon = Icons.Filled.Assessment,
            page = ActivityPage()
        ),
//        TabItem(
//            title = "Accounts",
//            icon = Icons.Outlined.SwitchAccount,
//            selectedIcon = Icons.Filled.SwitchAccount,
//            page = AccountsPage()
//        )
    )

    private fun refreshTab(tabIndex: Int) {
        tabItems[tabIndex].page.refresh(this@HomeActivity)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        refreshTab(selectedTabIndex.intValue)

        setContent {
            ClassUnchartedTheme {
                AndroidAnnoyance(topBar = {
                    TopAppBar(
                        title = {
                            val loggedInAsText = buildAnnotatedString {
                                append("Logged in as ${LoginManager.user?.name}.")
                                append(" ")

                                pushStringAnnotation(tag = "Clickable", annotation = "NotYou")
                                withStyle(
                                    style = SpanStyle(
                                        textDecoration = TextDecoration.Underline,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    append("Not you?")
                                }
                            }

                            Column {
                                Text(
                                    tabItems[selectedTabIndex.intValue].title,
                                    style = typography.titleMedium
                                )
                                ClickableText(
                                    text = loggedInAsText,
                                    style = typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface),
                                    onClick = { offset ->
                                        loggedInAsText.getStringAnnotations(
                                            tag = "Clickable",
                                            start = offset,
                                            end = offset
                                        ).firstOrNull()?.let {
                                            if (it.item == "NotYou") {
                                                LoginManager.logOutUser(this@HomeActivity)

                                                startActivity(
                                                    Intent(
                                                        this@HomeActivity,
                                                        MainActivity::class.java
                                                    )
                                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                                )
                                            }
                                        }
                                    }
                                )
                            }
                        },
                        actions = {
                            // Account switcher button
                            IconButton(onClick = {
                                AccountDialog().apply {
                                    show(supportFragmentManager, "AccountSheet")
                                }
                            }) {
                                Icon(Icons.Rounded.AccountCircle, contentDescription = "Accounts")
                            }

                            // Settings button
                            IconButton(onClick = {
                                startActivity(
                                    Intent(
                                        this@HomeActivity,
                                        SettingsActivity::class.java
                                    )
                                )
                            }) {
                                Icon(Icons.Rounded.Settings, contentDescription = "Settings")
                            }
                        }
                    )
                }, bottomBar = {
//                    NavigationBar {
//                        tabItems.forEachIndexed { index, tabItem ->
//                            NavigationBarItem(onClick = {
//                                selectedTabIndex.intValue = index
//                                refreshTab(index)
//                            }, icon = {
//                                Icon(
//                                    imageVector =
//                                    if (selectedTabIndex.intValue == index)
//                                        tabItem.selectedIcon
//                                    else tabItem.icon,
//                                    contentDescription = tabItem.title
//                                )
//                            }, selected = selectedTabIndex.intValue == index, label = {
//                                Text(tabItem.title, style = typography.bodySmall)
//                            })
//                        }
//                    }
                }) {
                    Column {
                        tabItems[selectedTabIndex.intValue].page.Content(
                            Modifier.padding(8.dp),
                            this@HomeActivity
                        )
                    }
                    Box(
                        modifier = Modifier.safeContentPadding().fillMaxSize(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        IconTabSwitcher(
                            icons = tabItems.map {
                                if (selectedTabIndex.intValue == tabItems.indexOf(
                                        it
                                    )
                                ) it.selectedIcon else it.icon
                            },
                            selectedTab = selectedTabIndex.intValue,
                            onTabSelected = {
                                selectedTabIndex.intValue = it
                                refreshTab(it)
                            }
                        )
                    }

                    LoadingIndicator(size = 12.dp)
                }
            }
        }

        if(!applicationContext.getSystemService(NotificationManager::class.java).areNotificationsEnabled()) {
            MaterialAlertDialogBuilder(this)
                .setTitle("Notifications Disabled")
                .setMessage("ClassUncharted notifications are disabled. They're literally one of the main features of ClassUncharted!! Please enable them to receive notifications.")
                .setPositiveButton("Enable") { _, _ ->
                    startActivity(Intent().apply {
                        action = "android.settings.APP_NOTIFICATION_SETTINGS"
                        putExtra("android.provider.extra.APP_PACKAGE", packageName)
                    })
                }
                .setNegativeButton("Cancel") { _, _ -> }
                .show()
        }
    }

//    override fun onDestroy() {
////        tabItems[selectedTabIndex.intValue].page.activeDialog?.dismiss()
//
//        super.onDestroy()
//    }
}