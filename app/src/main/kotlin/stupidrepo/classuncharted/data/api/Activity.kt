package stupidrepo.classuncharted.data.api

import androidx.room.Entity
import androidx.room.PrimaryKey
import stupidrepo.classuncharted.utils.DateUtils
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
data class Activity (
    @PrimaryKey val id: Int,
    val score: Int,

    val type: String,
    val polarity: String,

    val reason: String,

    val timestamp: String,

    val lesson_name: String?,
    val teacher_name: String?,

    val detention_date: String? = null,
    val detention_time: String? = null,
    val detention_location: String? = null,
    val detention_type: String? = null
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