package stupidrepo.classuncharted.data.api

import kotlinx.serialization.Serializable
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Serializable
data class Lesson(
    val teacher_name: String = "No data.",

    val subject_name: String = "No data.",
    val room_name: String = "No data.",

    val start_time: String,
    val end_time: String,
) {
    val format_start_time: LocalTime
        get() {
            val time = LocalTime.parse(start_time, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            return time
        }

    val format_end_time: LocalTime
        get() {
            val time = LocalTime.parse(end_time, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            return time
        }
}
