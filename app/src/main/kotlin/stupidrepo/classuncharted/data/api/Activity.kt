package stupidrepo.classuncharted.data.api

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import stupidrepo.classuncharted.utils.DateUtils
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Serializable
data class Activity (
    @PrimaryKey val id: Int,
    val score: Int,

    val type: String,
    val polarity: String,

    val reason: String,

    val timestamp: String,

    val lesson_name: String? = "No data.",
    val teacher_name: String? = "No data.",

    val detention_date: String? = "0000-00-00",
    val detention_time: String? = "0000-00-00 00:00:00",
    val detention_location: String? = "No location.",
    val detention_type: String? = "No detention type specified."
) {
    val format_timestamp: LocalDateTime
        get() {
            return DateUtils.convertAPIDateTime(timestamp, "uuuu-MM-dd HH:mm:ss")
        }

    val format_detention_date: LocalDate
        get() {
            return DateUtils.convertAPIDate(detention_date!!, "uuuu-MM-dd")
        }
}