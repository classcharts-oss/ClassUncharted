package stupidrepo.classuncharted.data.api.converters

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import stupidrepo.classuncharted.JSON
import stupidrepo.classuncharted.data.api.Attachment

class AttachmentsConverter {
    @TypeConverter
    fun toAttachments(json: String): List<Attachment> {
        val attachments = JSON.decodeFromString<List<Attachment>>(json)
        return attachments
    }

    @TypeConverter
    fun toString(attachments: List<Attachment>): String {
        return JSON.encodeToString(attachments)
    }
}
