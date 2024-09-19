package stupidrepo.classuncharted.data.api.converters

import androidx.room.TypeConverter
import stupidrepo.classuncharted.data.api.DetentionType

class DetentionTypeConverter {
    @TypeConverter
    fun toDetentionType(name: String): DetentionType {
        return DetentionType(name)
    }

    @TypeConverter
    fun toString(detentionType: DetentionType): String {
        return detentionType.name
    }
}
