package map.together.model

import android.os.Parcel
import android.os.Parcelable
import map.together.dto.UserSignUpDto

data class UserSignUpInfo(
        val email: String,
        val password: String,
        val confirmPassword: String,
        val userName: String,
) : Parcelable {

    companion object CREATOR : Parcelable.Creator<UserSignUpInfo> {
        private const val NAME_UNKNOWN = ""

        override fun createFromParcel(parcel: Parcel): UserSignUpInfo {
            return UserSignUpInfo(parcel)
        }

        override fun newArray(size: Int): Array<UserSignUpInfo?> {
            return arrayOfNulls(size)
        }
    }

    constructor(parcel: Parcel) : this(
            parcel.readString() ?: NAME_UNKNOWN,
            parcel.readString() ?: NAME_UNKNOWN,
            parcel.readString() ?: NAME_UNKNOWN,
            parcel.readString() ?: NAME_UNKNOWN,
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.apply {
            writeString(email)
            writeString(password)
            writeString(confirmPassword)
            writeString(userName)
        }
    }

    override fun describeContents(): Int = 0

    fun toUserSignUpDto(): UserSignUpDto = UserSignUpDto(
            email,
            password,
            userName,
    )
}