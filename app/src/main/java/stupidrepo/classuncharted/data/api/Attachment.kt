package stupidrepo.classuncharted.data.api

import android.os.Parcel
import android.os.Parcelable

data class Attachment(
    val filename: String,
    val url: String
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(filename)
        parcel.writeString(url)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Attachment> {
        override fun createFromParcel(parcel: Parcel): Attachment {
            return Attachment(parcel)
        }

        override fun newArray(size: Int): Array<Attachment?> {
            return arrayOfNulls(size)
        }
    }
}