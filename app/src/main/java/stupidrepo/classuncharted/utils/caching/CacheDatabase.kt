package stupidrepo.classuncharted.utils.caching

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import stupidrepo.classuncharted.data.api.Activity
import stupidrepo.classuncharted.data.api.Announcement
import stupidrepo.classuncharted.data.api.Detention
import stupidrepo.classuncharted.data.api.DetentionAttended
import stupidrepo.classuncharted.data.api.converters.AttachmentsConverter
import stupidrepo.classuncharted.data.api.converters.DetentionAttendedConverter
import stupidrepo.classuncharted.data.api.converters.DetentionTypeConverter
import stupidrepo.classuncharted.data.api.converters.LessonPupilBehaviourConverter
import stupidrepo.classuncharted.data.api.converters.TeacherConverter

@Database(
    entities = [Detention::class, Activity::class, Announcement::class],
    version = 5
)
@TypeConverters(
    TeacherConverter::class,
    LessonPupilBehaviourConverter::class,
    DetentionTypeConverter::class,
    AttachmentsConverter::class,
    DetentionAttendedConverter::class
)
abstract class CacheDatabase : RoomDatabase() {
    abstract fun detentionDAO(): DetentionDAO
    abstract fun activityDAO(): ActivityDAO
    abstract fun announcementDAO(): AnnouncementDAO
}