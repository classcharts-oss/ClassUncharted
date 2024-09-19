package stupidrepo.classuncharted.data.api

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import stupidrepo.classuncharted.data.api.converters.HomeworkStatusConverter
import stupidrepo.classuncharted.utils.DateUtils
import java.time.LocalDate

@Entity
data class Homework(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String,

    val teacher: String,
    val lesson: String,
    val subject: String,

    val issue_date: String,
    val due_date: String,

    @TypeConverters(HomeworkStatusConverter::class) val status: HomeworkStatus
) {
    val format_due: LocalDate
        get() = DateUtils.convertAPIDate(due_date, "uuuu-MM-dd")

    val format_issued: LocalDate
        get() = DateUtils.convertAPIDate(issue_date, "uuuu-MM-dd")
}