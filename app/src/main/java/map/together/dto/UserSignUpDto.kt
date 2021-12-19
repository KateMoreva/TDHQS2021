package map.together.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody

data class UserSignUpDto(
        @SerializedName("email")
        @Expose
        val email: String,
        @SerializedName("passwordHash")
        @Expose
        val password: String,
        @SerializedName("userName")
        @Expose
        val userName: String,
        @SerializedName("photoUrl")
        @Expose
        val photoUrl: String?,
)
