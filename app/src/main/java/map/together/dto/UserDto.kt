package map.together.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import map.together.model.UserInfo


data class UserDto(
        @SerializedName("id")
        @Expose
        val id: Long,
        @SerializedName("userName")
        @Expose
        var userName: String,
        @SerializedName("email")
        @Expose
        val email: String,
        @SerializedName("photoUrl")
        @Expose
        var pictureUrlStr: String?
) {

    fun toUserInfo(): UserInfo = UserInfo(
            id,
            userName,
            email,
            pictureUrlStr
    )
}