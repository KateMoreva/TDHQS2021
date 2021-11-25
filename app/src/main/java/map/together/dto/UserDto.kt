package map.together.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import map.together.model.UserInfo


data class UserDto(
        @SerializedName("id")
        @Expose
        val id: Long,
        @SerializedName("uid")
        @Expose
        val uid: String?,
        @SerializedName("user_name")
        @Expose
        var userName: String,
        @SerializedName("email")
        @Expose
        val email: String,
        @SerializedName("avatarUrl")
        @Expose
        var pictureUrlStr: String?
) {

    fun toUserInfo(): UserInfo = UserInfo(
            id,
            uid ?: "-1",
            userName,
            email,
            pictureUrlStr
    )
}