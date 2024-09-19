package stupidrepo.classuncharted.utils.caching

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import stupidrepo.classuncharted.data.api.Activity
import stupidrepo.classuncharted.data.api.Announcement

@Dao
interface AnnouncementDAO {
    @Query("SELECT * FROM announcement")
    suspend fun getAll(): List<Announcement>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(announcements: List<Announcement>)
}