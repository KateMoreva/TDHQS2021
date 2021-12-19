package map.together.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UserSignUpDto(
        @SerializedName("email")
        @Expose
        val email: String,
        @SerializedName("password")
        @Expose
        val password: String,
        @SerializedName("user_name")
        @Expose
        val userName: String,
)
