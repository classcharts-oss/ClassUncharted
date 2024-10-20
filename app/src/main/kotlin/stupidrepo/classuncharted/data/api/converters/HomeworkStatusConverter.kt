package stupidrepo.classuncharted.data.api.converters

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import stupidrepo.classuncharted.JSON
import stupidrepo.classuncharted.data.api.HomeworkStatus

class HomeworkStatusConverter {
    @TypeConverter
    fun toHomeworkStatus(json: String): HomeworkStatus {
        val homeworkStatus = JSON.decodeFromString<HomeworkStatus>(json)
        return homeworkStatus
    }

    @TypeConverter
    fun toString(homeworkStatus: HomeworkStatus): String {
        return JSON.encodeToString(homeworkStatus)
    }
}
