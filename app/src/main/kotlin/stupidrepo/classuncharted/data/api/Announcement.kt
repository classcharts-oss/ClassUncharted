package stupidrepo.classuncharted.data.api

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import stupidrepo.classuncharted.data.api.converters.AttachmentsConverter
import stupidrepo.classuncharted.utils.DateUtils
import java.time.LocalDateTime

@Entity
@Serializable
data class Announcement(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String,
    val teacher_name: String,
    val timestamp: String,

    @Contextual @TypeConverters(AttachmentsConverter::class) val attachments: List<Attachment>
): Parcelable {
    val format_timestamp: LocalDateTime
        get() = DateUtils.convertAPIDateTime(timestamp)

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        "",
        "",
        "",
        "2011-12-03T10:15:30",
        mutableListOf()
//        parcel.readString() ?: "",
//        parcel.readString() ?: "",
//        parcel.readString() ?: "",
//        parcel.readString() ?: "",
//        mutableListOf<Attachment>().apply {
//            parcel.readTypedList(this, Attachment.CREATOR)
//        }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Announcement> {
        override fun createFromParcel(parcel: Parcel): Announcement {
            return Announcement(parcel)
        }

        override fun newArray(size: Int): Array<Announcement?> {
            return arrayOfNulls(size)
        }
    }
}