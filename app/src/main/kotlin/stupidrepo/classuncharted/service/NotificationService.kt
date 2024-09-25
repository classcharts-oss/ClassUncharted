package stupidrepo.classuncharted.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import stupidrepo.classuncharted.MyApplication
import stupidrepo.classuncharted.R
import stupidrepo.classuncharted.data.mine.MyChannels
import stupidrepo.classuncharted.data.mine.NotificationInfo
import stupidrepo.classuncharted.managers.LoginManager
import stupidrepo.classuncharted.managers.LoginManager.logInUser
import stupidrepo.classuncharted.settings.DisableServiceSetting
import stupidrepo.classuncharted.utils.NotificationUtils.makeNotification
import stupidrepo.classuncharted.utils.NotificationUtils.sendNotification
import stupidrepo.classuncharted.utils.caching.CacheUtils
import stupidrepo.classuncharted.utils.caching.CacheUtils.cacheDB
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class NotificationService : Service() {
    private val TAG = "NotificationService"

    private val delay = 60L * 5

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    private val executorService = Executors.newSingleThreadScheduledExecutor()

    private val multipleDTs = abs("multiple_detentions".hashCode())
    private val multipleACTs = abs("multiple_activities".hashCode())
    private val multipleANNs = abs("multiple_announcements".hashCode())

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val settingsManager = (applicationContext as MyApplication).SettingsManager
        if(((settingsManager.getSetting(DisableServiceSetting::class)?.value ?: false) as Boolean)) {
            stopSelf()
            return START_NOT_STICKY
        }

        startForeground(1, makeNotification(
            applicationContext,
            MyChannels.ServiceChannel,
            NotificationInfo(
                0, // NOTE: the id here isn't used at all so we can set it to 0
                "Background Service Running",
                "Currently running in background. Feel free to swipe away this notification."
            )
        ).setSmallIcon(R.drawable.ic_service).build())

        executorService.scheduleWithFixedDelay({
            scope.launch { this@NotificationService.doWork() }
        }, delay/2, delay, TimeUnit.SECONDS) // 180 = ClassCharts API recommended refresh time
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        executorService.shutdown()
        job.cancel()
    }

    private fun doWork() {
        CoroutineScope(Dispatchers.Main).launch {
            val account = LoginManager.user?.account ?: return@launch

            logInUser(
                applicationContext,
                account, {
                    checkDetentions()
                    checkActivity()
                    checkAnnouncements()
                }, {
                    sendNotification(
                        applicationContext,
                        makeNotification(
                            applicationContext,
                            MyChannels.OtherChannel,
                            NotificationInfo(
                                id = -1,
                                title = "Failed to log in!",
                                message = "Couldn't log in. " +
                                        "Check your ClassCharts login details " +
                                        "or your internet connection."
                            )
                        ).setSmallIcon(android.R.drawable.stat_sys_warning),
                        -1
                    )
                },
                logInAnyway = true
            )
        }
    }

    private fun checkDetentions() {
        Log.d(TAG, "checkDetentions: Checking for new detentions...")
        CoroutineScope(Dispatchers.Main).launch {
            val currentDTs = LoginManager.user?.getDetentions()
            val cachedDTs = CacheUtils.getDetentions()
            val newDTs = currentDTs?.filter { dt ->
                cachedDTs.none { it.id == dt.id }
            }

            newDTs?.let {
                Log.d(TAG, "checkDetentions: Found ${newDTs.size} new detentions!")

                if(it.size > 1) {
                    sendNotification(applicationContext,
                        "Multiple New Detentions",
                        "You have ${it.size} new detentions.",
                        MyChannels.DetentionChannel,
                        R.drawable.ic_dt,
                        multipleDTs
                    )
                } else if(it.isNotEmpty()) {
                    it.first().let { dt ->
                        sendNotification(applicationContext,
                            "New Detention",
                            "Set by ${dt.teacher.full_name} for ${dt.lesson_pupil_behaviour.reason}. View more details in-app.",
                            MyChannels.DetentionChannel,
                            R.drawable.ic_dt,
                            dt.id
                        )
                    }
                }
            }

            cacheDB?.detentionDAO()?.insertAll(newDTs ?: emptyList())
        }
    }

    private fun checkActivity() {
        Log.d(TAG, "checkActivity: Checking for new activity...")
        CoroutineScope(Dispatchers.Main).launch {
            val currentActivities = LoginManager.user?.getActivities()
            val cachedActivities = CacheUtils.getActivities()
            val newActivities = currentActivities?.filter { act -> cachedActivities.none { it.id == act.id } }

            Log.d(TAG, "checkActivity: Found ${newActivities?.size} new activities!")

            newActivities?.let {
                if(it.size > 1) {
                    sendNotification(applicationContext,
                        "Multiple New Activities",
                        "You have ${it.size} new activities.",
                        MyChannels.ActivityChannel,
                        R.drawable.ic_activity,
                        multipleACTs
                    )
                } else if(it.isNotEmpty()) {
                    it.first().let { act ->
                        sendNotification(applicationContext,
                            "New Activity",
                            "${if (act.teacher_name != null) "${act.teacher_name} awarded you" else "You were awarded" } ${act.score} point${if (act.score != 1) "s" else ""} for ${act.reason}.",
                            MyChannels.ActivityChannel,
                            with(act.polarity) {
                                when {
                                    this == "positive" -> R.drawable.ic_activity_positive
                                    this == "negative" -> R.drawable.ic_activity_negative
                                    else -> R.drawable.ic_activity
                                }
                            },
                            act.id
                        )
                    }
                }
            }

            cacheDB?.activityDAO()?.insertAll(newActivities ?: emptyList())
        }
    }

    private fun checkAnnouncements() {
        Log.d(TAG, "checkAnnouncements: Checking for new announcements...")
        CoroutineScope(Dispatchers.Main).launch {
            val currentAnnouncements = LoginManager.user?.getAnnouncements()
            val cachedAnnouncements = CacheUtils.getAnnouncements()
            val newAnnouncements = currentAnnouncements?.filter { ann -> cachedAnnouncements.none { it.id == ann.id } }

            Log.d(TAG, "checkAnnouncements: Found ${newAnnouncements?.size} new announcements!")

            newAnnouncements?.let {
                if(it.size > 1) {
                    sendNotification(applicationContext,
                        "Multiple New Announcements",
                        "You have ${it.size} new announcements.",
                        MyChannels.AnnouncementsChannel,
                        R.drawable.ic_announcements,
                        multipleANNs
                    )
                } else if(it.isNotEmpty()) {
                    it.first().let { ann ->
                        sendNotification(applicationContext,
                            "New Announcement",
                            "\"${ann.title}\" (from ${ann.teacher_name}).",
                            MyChannels.ActivityChannel,
                            R.drawable.ic_announcements,
                            ann.id
                        )
                    }
                }
            }

            cacheDB?.announcementDAO()?.insertAll(newAnnouncements ?: emptyList())
        }
    }
}