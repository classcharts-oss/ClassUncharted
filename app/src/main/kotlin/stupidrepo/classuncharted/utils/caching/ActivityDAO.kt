package stupidrepo.classuncharted.utils.caching

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import stupidrepo.classuncharted.data.api.Activity

@Dao
interface ActivityDAO {
    @Query("SELECT * FROM activity")
    suspend fun getAll(): List<Activity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(detentions: List<Activity>)
}