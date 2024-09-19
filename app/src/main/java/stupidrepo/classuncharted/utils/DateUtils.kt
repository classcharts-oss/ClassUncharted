@file:Suppress("LocalVariableName")

package stupidrepo.classuncharted.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateUtils {
    fun convertAPIDateTime(dateTime: String, format: String): LocalDateTime {
        val dateFormat = DateTimeFormatter.ofPattern(format, Locale.ENGLISH)
        return LocalDateTime.parse(dateTime, dateFormat)
    }

    fun convertAPIDate(date: String, format: String): LocalDate {
        val dateFormat = DateTimeFormatter.ofPattern(format, Locale.ENGLISH)
        return LocalDate.parse(date, dateFormat)
    }

    fun convertAPIDateTime(date: String): LocalDateTime {
        return LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME)
    }

    fun getGroupsForPastTodayFuture(dates: List<LocalDateTime>): Triple<List<LocalDateTime>, List<LocalDateTime>, List<LocalDateTime>> {
        val today = LocalDateTime.now()
        val past = mutableListOf<LocalDateTime>()
        val todayList = mutableListOf<LocalDateTime>()
        val future = mutableListOf<LocalDateTime>()
        dates.forEach {
            when {
                it.isBefore(today) -> past.add(it)
                it.isAfter(today) -> future.add(it)
                else -> todayList.add(it)
            }
        }
        return Triple(past, todayList, future)
    }
}