package stupidrepo.classuncharted.data.api

import kotlinx.serialization.Serializable
import stupidrepo.classuncharted.data.mine.Account
import stupidrepo.classuncharted.managers.APIManager
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val TAG = "User"

@Serializable
data class User (
    val id: Int,
    val name: String,
) {
    var session_id: String? = null
    var account: Account? = null

    fun getDetentions(): List<Detention> {
        return APIManager.GET<Detention>("detentions")
    }

    fun getActivities(): List<Activity> {
        return APIManager.GET<Activity>(
            "activity",
            listOf("from=${LocalDateTime.now().minusYears(2).format(DateTimeFormatter.ofPattern("uuuu-MM-dd"))}")
        )
    }

    fun getMoreActivities(lastID: Int): List<Activity> {
        return APIManager.GET<Activity>(
            "activity",
            listOf("from=${LocalDateTime.now().minusYears(2).format(DateTimeFormatter.ofPattern("uuuu-MM-dd"))}", "last_id=$lastID")
        )
    }

    fun getLessons(date: LocalDate): List<Lesson> {
        return APIManager.GET<Lesson>(
            "timetable",
            listOf("date=" + date.format(DateTimeFormatter.ofPattern("uuuu-MM-dd")))
        )
    }

    fun getAnnouncements(): List<Announcement> {
        return APIManager.GET<Announcement>("announcements")
    }

    fun getHomework(): List<Homework> {
        return APIManager.GET<Homework>("homeworks")
    }
}