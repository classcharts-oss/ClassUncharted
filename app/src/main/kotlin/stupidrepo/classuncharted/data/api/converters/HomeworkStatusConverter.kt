package stupidrepo.classuncharted.data.api.converters

import androidx.room.TypeConverter
import stupidrepo.classuncharted.data.api.Attachment
import stupidrepo.classuncharted.data.api.Homework
import stupidrepo.classuncharted.data.api.HomeworkStatus
import stupidrepo.classuncharted.utils.JSONUtils

class HomeworkStatusConverter {
    @TypeConverter
    fun toHomeworkStatus(json: String): HomeworkStatus {
        val homeworkStatus = JSONUtils.gson.fromJson(json, HomeworkStatus::class.java)
        return homeworkStatus
    }

    @TypeConverter
    fun toString(homeworkStatus: HomeworkStatus): String {
        return JSONUtils.gson.toJson(homeworkStatus)
    }
}
