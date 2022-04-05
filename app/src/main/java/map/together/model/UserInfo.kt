package map.together.model

import android.os.Parcel
import android.os.Parcelable
import map.together.dto.UserDto

data class UserInfo(
        val id: Long,
        val userName: String,
        val email: String,
        val photoUrl: String?
) : Parcelable {

    companion object CREATOR : Parcelable.Creator<UserInfo> {
        private const val NAME_UNKNOWN = ""

        override fun createFromParcel(parcel: Parcel): UserInfo {
            return UserInfo(parcel)
        }

        override fun newArray(size: Int): Array<UserInfo?> {
            return arrayOfNulls(size)
        }
    }

    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readString() ?: NAME_UNKNOWN,
            parcel.readString() ?: NAME_UNKNOWN,
            parcel.readString() ?: NAME_UNKNOWN
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.apply {
            writeLong(id)
            writeString(userName)
            writeString(email)
            writeString(photoUrl)
        }
    }

    override fun describeContents(): Int = 0

    fun toUserDto(): UserDto = UserDto(
            id,
            userName,
            email,
            photoUrl
    )
}