package stupidrepo.classuncharted.utils.caching

import android.content.Context
import android.util.Log
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import stupidrepo.classuncharted.data.api.Activity
import stupidrepo.classuncharted.data.api.Announcement
import stupidrepo.classuncharted.data.api.Detention

object CacheUtils {
    private const val TAG = "CacheManager"
    var cacheDB : CacheDatabase? = null

    fun initCache(context: Context) {
        cacheDB = Room.databaseBuilder(
            context.applicationContext,
            CacheDatabase::class.java, "cache-db"
        ).fallbackToDestructiveMigration().build()
        Log.d(TAG, "initCache: Cache initialized.")
    }

    fun getDetentions(): List<Detention> {
        Log.d(TAG, "getDetentions: Getting detentions from cache...")
        return runBlocking {
            withContext(Dispatchers.IO) {
                val detentions = cacheDB?.detentionDAO()?.getAll() ?: emptyList()
                Log.d(TAG, "getDetentions: Retrieved ${detentions.size} detentions from cache.")

                detentions
            }
        }
    }

    fun cacheDetentions(detentions: List<Detention>) {
        Log.d(TAG, "cacheDetentions: Caching detentions...")
        CoroutineScope(Dispatchers.Main).launch {
            cacheDB?.detentionDAO()?.insertAll(detentions)
            Log.d(TAG, "cacheDetentions: Cached ${detentions.size} detentions.")
        }
    }

    fun getActivities(): List<Activity> {
        Log.d(TAG, "getActivities: Getting activities from cache...")
        return runBlocking {
            withContext(Dispatchers.IO) {
                val activities = cacheDB?.activityDAO()?.getAll() ?: emptyList()
                Log.d(TAG, "getActivities: Retrieved ${activities.size} activities from cache.")

                activities
            }
        }
    }

    fun cacheActivities(activities: List<Activity>) {
        Log.d(TAG, "cacheActivities: Caching activities...")
        CoroutineScope(Dispatchers.Main).launch {
            cacheDB?.activityDAO()?.insertAll(activities)
            Log.d(TAG, "cacheActivities: Cached ${activities.size} activities.")
        }
    }

    fun getAnnouncements(): List<Announcement> {
        Log.d(TAG, "getAnnouncements: Getting announcements from cache...")
        return runBlocking {
            withContext(Dispatchers.IO) {
                val announcements = cacheDB?.announcementDAO()?.getAll() ?: emptyList()
                Log.d(TAG, "getAnnouncements: Retrieved ${announcements.size} announcements from cache.")

                announcements
            }
        }
    }

    fun cacheAnnouncements(announcements: List<Announcement>) {
        Log.d(TAG, "cacheAnnouncements: Caching announcements...")
        CoroutineScope(Dispatchers.Main).launch {
            cacheDB?.announcementDAO()?.insertAll(announcements)
            Log.d(TAG, "cacheAnnouncements: Cached ${announcements.size} announcements.")
        }
    }

    fun clearCache() {
        Log.d(TAG, "clearCache: Clearing cache...")
        CoroutineScope(Dispatchers.Main).launch {
            cacheDB?.clearAllTables()
            Log.d(TAG, "clearCache: Cache cleared.")
        }
    }
}