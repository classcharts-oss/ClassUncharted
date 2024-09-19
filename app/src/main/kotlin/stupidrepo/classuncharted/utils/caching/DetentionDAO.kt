package stupidrepo.classuncharted.utils.caching

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import stupidrepo.classuncharted.data.api.Detention

@Dao
interface DetentionDAO {
    @Query("SELECT * FROM detention")
    suspend fun getAll(): List<Detention>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(detentions: List<Detention>)
}