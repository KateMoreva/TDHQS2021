package map.together.dto.db

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class UserMapDto(
        @SerializedName("id")
        @Expose
        val id: Long,
        @SerializedName("email")
        @Expose
        var email: String,
        @SerializedName("userName")
        @Expose
        var userName: String,
        @SerializedName("photoUrl")
        @Expose
        var photoUrl: String?,
        @SerializedName("roleName")
        @Expose
        var roleName: String?,
)