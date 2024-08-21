import android.os.Parcel
import android.os.Parcelable

data class Artista(
    val name: String,
    val album: String,
    val songCount: Int,
    val filePath: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(album)
        parcel.writeInt(songCount)
        parcel.writeString(filePath)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Artista> {
        override fun createFromParcel(parcel: Parcel): Artista {
            return Artista(parcel)
        }

        override fun newArray(size: Int): Array<Artista?> {
            return arrayOfNulls(size)
        }
    }
}
