package stupidrepo.classuncharted.data.api.converters

import androidx.room.TypeConverter
import stupidrepo.classuncharted.data.api.Teacher

class TeacherConverter {
    @TypeConverter
    fun toTeacher(fullName: String): Teacher {
        val split = fullName.split(" ")
        return Teacher(split[3].toInt(), split[0], split[1], split[2])
    }

    @TypeConverter
    fun toString(teacher: Teacher): String {
        return teacher.full_name + " " + teacher.id
    }
}