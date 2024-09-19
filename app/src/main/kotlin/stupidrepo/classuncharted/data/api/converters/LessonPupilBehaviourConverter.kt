package stupidrepo.classuncharted.data.api.converters

import androidx.room.TypeConverter
import stupidrepo.classuncharted.data.api.LessonPupilBehaviour

class LessonPupilBehaviourConverter {
    @TypeConverter
    fun toLessonPupilBehaviour(reason: String): LessonPupilBehaviour {
        return LessonPupilBehaviour(reason)
    }

    @TypeConverter
    fun toString(lessonPupilBehaviour: LessonPupilBehaviour): String {
        return lessonPupilBehaviour.reason
    }
}