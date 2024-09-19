package stupidrepo.classuncharted.data.api.converters

import androidx.room.TypeConverter
import stupidrepo.classuncharted.data.api.Attachment
import stupidrepo.classuncharted.utils.JSONUtils

class AttachmentsConverter {
    @TypeConverter
    fun toAttachments(json: String): List<Attachment> {
        val attachments = JSONUtils.gson.fromJson(json, Array<Attachment>::class.java)
        return attachments.toList()
    }

    @TypeConverter
    fun toString(attachments: List<Attachment>): String {
        return JSONUtils.gson.toJson(attachments)
    }
}
