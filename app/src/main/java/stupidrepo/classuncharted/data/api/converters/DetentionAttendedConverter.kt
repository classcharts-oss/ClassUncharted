package stupidrepo.classuncharted.data.api.converters

import androidx.room.TypeConverter
import stupidrepo.classuncharted.data.api.Attachment
import stupidrepo.classuncharted.data.api.DetentionAttended
import stupidrepo.classuncharted.utils.JSONUtils

class DetentionAttendedConverter {
    @TypeConverter
    fun toDetentionAttended(attended: String): DetentionAttended {
        var returnVal = DetentionAttended.unknown

        try {
            returnVal = DetentionAttended.valueOf(attended)
        } catch (_: IllegalArgumentException) {}
        return returnVal
    }

    @TypeConverter
    fun toString(detentionAttended: DetentionAttended): String {
        return detentionAttended.name
    }
}