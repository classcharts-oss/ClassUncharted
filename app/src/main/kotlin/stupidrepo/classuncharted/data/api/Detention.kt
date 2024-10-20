package stupidrepo.classuncharted.data.api

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import stupidrepo.classuncharted.data.api.converters.DetentionAttendedConverter
import stupidrepo.classuncharted.data.api.converters.DetentionTypeConverter
import stupidrepo.classuncharted.data.api.converters.LessonPupilBehaviourConverter
import stupidrepo.classuncharted.data.api.converters.TeacherConverter
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Serializable
class Detention(
    @PrimaryKey val id: Int,
    val date: String,
    val time: String,
    val length: Int,

    val location: String,

    @TypeConverters(TeacherConverter::class) val teacher: Teacher,
    @Contextual @TypeConverters(LessonPupilBehaviourConverter::class) val lesson_pupil_behaviour: LessonPupilBehaviour,
    @TypeConverters(DetentionTypeConverter::class) val detention_type: DetentionType? = null,

    @TypeConverters(DetentionAttendedConverter::class) val attended: DetentionAttended
) {
    fun getAttendedString(): String {
        return when(attended) {
            DetentionAttended.pending -> "Pending"
            DetentionAttended.yes -> "Attended"
            DetentionAttended.no -> "Missed/Unattended"
            DetentionAttended.upscaled -> "Upscaled"

            else -> "Unknown"
        }
    }

    val formatted_date_time: String
        get() {
            try {
                return LocalDateTime.parse(date, java.time.format.DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ssXXX")).format(java.time.format.DateTimeFormatter.ofPattern("E, d MMM uuuu")) + " " + time
            } catch (e: Exception) {
                return date
            }
        }

    val local_date: LocalDate
        get() {
            return LocalDate.parse(date, java.time.format.DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ssXXX"))
        }
}