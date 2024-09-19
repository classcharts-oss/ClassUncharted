package stupidrepo.classuncharted.data.api

import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class Lesson(
    val teacher_name: String,

    val subject_name: String,
    val room_name: String,

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
